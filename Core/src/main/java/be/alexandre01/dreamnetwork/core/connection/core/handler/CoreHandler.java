package be.alexandre01.dreamnetwork.core.connection.core.handler;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.connection.core.handler.ICoreHandler;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.screen.IScreen;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.connection.core.communication.services.AuthentificationResponse;
import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreResponse;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.service.JVMContainer;
import be.alexandre01.dreamnetwork.core.service.screen.ScreenManager;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import be.alexandre01.dreamnetwork.utils.Tuple;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import lombok.Setter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

import static be.alexandre01.dreamnetwork.api.console.Console.*;



public class CoreHandler extends ChannelInboundHandlerAdapter implements ICoreHandler {


    @Getter
    private ArrayList<CoreResponse> responses = new ArrayList<>();

    @Getter private CallbackManager callbackManager;
    @Getter
    private static ArrayList<CoreResponse> globalResponses = new ArrayList<>();
    @Setter
    @Getter
    private boolean hasDevUtilSoftwareAccess = false;
    @Getter
    private ArrayList<ChannelHandlerContext> allowedCTX = new ArrayList<>();
    private AuthentificationResponse authResponse;
    @Getter
    private ArrayList<ChannelHandlerContext> externalConnections = new ArrayList<>();
    //A PATCH
    private HashMap<Message, Tuple<Channel, GenericFutureListener<? extends Future<? super Void>>>> queue = new HashMap<>();
    private final Core core;

    public CoreHandler() {
        this.core = Core.getInstance();
        this.callbackManager = new CallbackManager();
        this.hasDevUtilSoftwareAccess = Core.getInstance().isDevToolsAccess();

        authResponse = new AuthentificationResponse(this);
    }

    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void channelRegistered(final ChannelHandlerContext ctx) {

        printLang("connection.core.handler.localAddress", Level.FINE, ctx.channel().localAddress());
        printLang("connection.core.handler.remoteAddress", Level.FINE, ctx.channel().remoteAddress());
      //  System.out.println(ctx.channel().remoteAddress().toString().split(":")[0]);

        String remote = ctx.channel().remoteAddress().toString().split(":")[0];
        if (!hasDevUtilSoftwareAccess) {
            if (!remote.replaceAll("/", "").equalsIgnoreCase("127.0.0.1")) {
                ctx.close();
            }
        } else {
            if (!remote.replaceAll("/", "").equalsIgnoreCase("127.0.0.1")) {
                externalConnections.add(ctx);
            }
        }
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) { // (1)
        printLang("connection.core.handler.channelActive", Level.FINE);
        print(ctx.channel().remoteAddress().toString().split(":")[0], Level.FINE);

        if (!queue.isEmpty()) {
            taskQueue();
        }
    }

    private void taskQueue() {
        Message msg = (Message) queue.keySet().toArray()[0];
        Tuple<Channel, GenericFutureListener<? extends Future<? super Void>>> t = null;
        Channel channel;
        if (queue.containsKey(msg)) {
            return;
        }
        t = queue.get(msg);
        channel = t.a();

        byte[] entry = msg.toString().getBytes(StandardCharsets.UTF_8);
        final ByteBuf buf = channel.alloc().buffer(entry.length);
        buf.writeBytes(entry);

        ChannelFuture future = channel.writeAndFlush(buf);
        future.addListener(f -> {
            queue.remove(msg);
            if (!queue.isEmpty()) {
                taskQueue();
            }
        });
        if (t.b() != null) {
            future.addListener(t.b());
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf m = (ByteBuf) msg; // (1)
        byte[] bytes = new byte[m.readableBytes()];
// Buffer data read into byte array
        m.readBytes(bytes);
        String s_to_decode = null;
        try {
            s_to_decode = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //TO DECODE STRING IF ENCODED AS AES

        //ctx.channel().closeFuture();

       /* if (!Message.isJSONValid(s_to_decode))
            return;*/

        //  System.out.println(s_to_decode);

        try {
            //ALLOWED CONNECTION
            if (allowedCTX.contains(ctx)) {
                Message message = Message.createFromJsonString(s_to_decode);
                if (message == null) {
                    return;
                }

                IClient client = core.getClientManager().getClient(ctx);

                if(client == null){
                    return;
                }

                if(client.getJvmService() != null){
                    fine(Colors.YELLOW+"Received message from " +Colors.CYAN_BOLD+ client.getJvmService().getFullName() + " : " +Colors.RESET+ message.toString());
                }else {
                    fine(Colors.YELLOW+"Received message from an "+Colors.CYAN_BOLD+"NON-CLIENT -> " + ctx.channel().remoteAddress().toString().split(":")[0] + " : " + Colors.RESET+message.toString());
                }
                //if message hasReceiver resend directly to the client
                if(message.hasReceiver()){
                    if(!message.getReceiver().equals("core")){
                        IClient receiver = core.getClientManager().getClient(message.getReceiver());
                        if(receiver == null){
                            Console.print("Receiver "+ message.getReceiver()+" is unknown");
                            return;
                        }
                        if(receiver.getJvmService() != null){
                            message.setProvider(receiver.getJvmService().getFullName());
                        }

                        receiver.writeAndFlush(message);
                        return;
                    }
                }

                //Receive and operate the message



               // core.getFileHandler().publish(new LogRecord(Level.INFO, "Received message from " + ctx.channel().remoteAddress().toString().split(":")[0] + " : " + message.toString()));
                for (int i = 0; i < responses.size(); i++) {
                    CoreResponse iBasicClientResponse = responses.get(i);
                    try {
                        iBasicClientResponse.onAutoResponse(message, ctx, client);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < globalResponses.size(); i++) {
                    CoreResponse iBasicClientResponse = globalResponses.get(i);
                    try {
                        iBasicClientResponse.onAutoResponse(message, ctx, client);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                //NOT ALLOWED CONNECTION
                try {

                    Message message = Message.createFromJsonString(s_to_decode);
                    authResponse.onAutoResponse(message, ctx, core.getClientManager().getClient(ctx));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } finally {
            m.release();
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

        fine("UNREGISTERED");

        //LOG
        if (allowedCTX.contains(ctx)) {
            HashMap<ChannelHandlerContext, IClient> clientByConnexion = Core.getInstance().getClientManager().getClientsByConnection();
            if (clientByConnexion.containsKey(ctx)) {
                IService jvmService = clientByConnexion.get(ctx).getJvmService();
                String name = null;
                if (jvmService != null) {
                    name = jvmService.getFullName();
                }
                if (clientByConnexion.get(ctx).isExternalTool()) {
                    name = "DEVTOOLS";
                }

                printLang("connection.core.handler.processStopped", name);


                //create an event for external stop event
               // core.getEventsFactory().callEvent(new CoreServiceStopEvent(core.getDnCoreAPI(), jvmService));
            }
        }

        if (executorService != null) {
            executorService.shutdown();
        }
        print(ctx.channel().remoteAddress(), Level.FINE);
        IClient client = Core.getInstance().getClientManager().getClient(ctx);


        //Remove Server
        printLang("connection.core.handler.removeClient", client);
        if (client != null) {
            client.getClientManager().getExternalTools().remove(client);
            if (!client.isExternalTool()) {
                String server = client.getJvmService().getFullName();
                for (IClient c : Core.getInstance().getClientManager().getClients().values()) {
                    if (c.getJvmType() == JVMContainer.JVMType.SERVER) {
                        c.getRequestManager().sendRequest(RequestType.SERVER_REMOVE_SERVERS, server);
                    }
                }
                for (IClient c : Core.getInstance().getClientManager().getExternalTools()) {
                    if (c != null) {
                        //c.getRequestManager().sendRequest(RequestType.DEV_TOOLS_NEW_SERVER, server+";"+client.getJvmService().getJvmExecutor().getType()+";"+ client.getJvmService().getJvmExecutor().isProxy()+";false");
                        c.getRequestManager().sendRequest(RequestType.DEV_TOOLS_REMOVE_SERVERS, server);
                    }
                }
            }
        }
        //UNREGISTER CHANNEL
        if (client != null) {
            Core.getInstance().getChannelManager().unregisterAllClientToChannel(client);
            //UNREGISTER PLAYER LISTENERS
            Core.getInstance().getServicePlayersManager().removeUpdatingClient(client);
        }


        //REMOVE SERVICES
        if (client != null && client.getJvmService() != null && !client.getJvmService().getScreen().isViewing()) {
            System.out.println("Removing service by handler");
            client.getJvmService().getJvmExecutor().removeService(client.getJvmService());
        }


        if (client != null && client.isExternalTool()) {
            ScreenManager screenManager = ScreenManager.instance;
            for (IScreen screen : screenManager.getScreens().values()) {
                screen.getDevToolsReading().remove(client);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        fine("Channel inactive");
        super.channelInactive(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        fine("Handler removed");
        ctx.close();
        super.handlerRemoved(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!Main.isDisabling()) {
            if (!(cause instanceof java.net.SocketException)) {
                print(cause.getMessage(), Level.FINE);
                cause.printStackTrace();
            }
        }
        ctx.close();
    }


    @Override
    public void writeAndFlush(Message msg, IClient client) {
        this.writeAndFlush(msg, null, client);
    }

    @Override
    public void writeAndFlush(Message msg, GenericFutureListener<? extends Future<? super Void>> listener, IClient client) {

        if (client != null && client.getJvmService() != null) {
            fine(Colors.YELLOW + "WRITE AND FLUSH for " + client.getJvmService().getFullName() + ">> " + Colors.RESET + msg);
        } else {
            fine(Colors.YELLOW + "WRITE AND FLUSH for UNKNOWN>> " + Colors.RESET + msg);
        }


        ChannelHandlerContext ctx = client.getChannelHandlerContext();
        //   Console.print(ctx, Level.FINE);
        if (ctx == null || !ctx.channel().isActive() || !queue.isEmpty()) {
            assert ctx != null;
            queue.put(msg, new Tuple<>(ctx.channel(), listener));
            return;
        }
        byte[] entry = msg.toString().getBytes(StandardCharsets.UTF_8);
        /*final ByteBuf buf = ctx.alloc().buffer(entry.length);
        buf.writeInt(msg.toString().length());
        buf.writeBytes(entry);*/
        if (listener == null) {
            ctx.writeAndFlush(entry);
            return;
        }

        ctx.writeAndFlush(entry).addListener(listener);
        //buf.release();
    }
}
