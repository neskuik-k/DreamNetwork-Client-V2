package be.alexandre01.dreamnetwork.client.connection.core.handler;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.connection.core.handler.ICoreHandler;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.Main;
import be.alexandre01.dreamnetwork.client.connection.core.communication.AuthentificationResponse;
import be.alexandre01.dreamnetwork.client.connection.core.communication.BaseResponse;
import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreResponse;
import be.alexandre01.dreamnetwork.api.connection.request.RequestType;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import be.alexandre01.dreamnetwork.client.service.screen.Screen;
import be.alexandre01.dreamnetwork.client.service.screen.ScreenManager;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
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

public class CoreHandler extends ChannelInboundHandlerAdapter implements ICoreHandler {

    private ArrayList<CoreResponse> responses = new ArrayList<>();
    @Setter @Getter private boolean hasDevUtilSoftwareAccess = false;
    @Getter private ArrayList<ChannelHandlerContext> allowedCTX = new ArrayList<>();
    private AuthentificationResponse authResponse;
   @Getter private ArrayList<ChannelHandlerContext> externalConnection = new ArrayList<>();
    //A PATCH
    private HashMap<Message, Tuple<Channel,GenericFutureListener<? extends Future<? super Void>>>> queue = new HashMap<>();
    private final Client client;
    public CoreHandler(){
        this.client = Client.getInstance();
        this.client.setCoreHandler(this);
        this.hasDevUtilSoftwareAccess = Client.getInstance().isDevToolsAccess();

        responses.add(new BaseResponse());
        authResponse = new AuthentificationResponse();
    }

    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    @Override
    public void channelRegistered(final ChannelHandlerContext ctx) {
        Console.print("Local ADRESS " + ctx.channel().localAddress(),Level.FINE);
        Console.print("Remote ADRESS " + ctx.channel().remoteAddress(),Level.FINE);


        String remote = ctx.channel().remoteAddress().toString().split(":")[0];
        if(!hasDevUtilSoftwareAccess){
            if(!remote.replaceAll("/","").equalsIgnoreCase("127.0.0.1")){
                ctx.close();
            }
        }else {
            if(!remote.replaceAll("/","").equalsIgnoreCase("127.0.0.1")){
                externalConnection.add(ctx);
            }
        }
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) { // (1)
        Console.print("CHANNEL ACTIVE",Level.FINE);
        Console.print(ctx.channel().remoteAddress().toString().split(":")[0],Level.FINE);


        if(!queue.isEmpty()){
            taskQueue();
        }
    }

    private void taskQueue(){
        Message msg = (Message) queue.keySet().toArray()[0];
        Tuple<Channel,GenericFutureListener<? extends Future<? super Void>>> t = null;
        Channel channel;
        if(queue.containsKey(msg)){
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
            if(!queue.isEmpty()){
                taskQueue();
            }
        });
        if(t.b() != null){
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

        ctx.channel().closeFuture();

        if(!Message.isJSONValid(s_to_decode))
            return;

      //  System.out.println(s_to_decode);

        try {
            //ALLOWED CONNECTION
            if(allowedCTX.contains(ctx)){
                Message message = Message.createFromJsonString(s_to_decode);
                for(CoreResponse iBasicClientResponse : responses){
                    try {
                        iBasicClientResponse.onResponse(message,ctx,client.getClientManager().getClient(ctx));
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            }else {
                //NOT ALLOWED CONNECTION
                try {
                    Message message = Message.createFromJsonString(s_to_decode);
                    authResponse.onResponse(message,ctx,client.getClientManager().getClient(ctx));
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

        //LOG
        if(allowedCTX.contains(ctx)){
            HashMap<ChannelHandlerContext, IClient> clientByConnexion = Client.getInstance().getClientManager().getClientsByConnection();
            if(clientByConnexion.containsKey(ctx)){
                IService jvmService = clientByConnexion.get(ctx).getJvmService();
                String name = null;
                if(jvmService != null){
                    name = jvmService.getJvmExecutor().getName()+"-"+jvmService.getId();
                }
                if(clientByConnexion.get(ctx).isDevTool()){
                    name = "DEVTOOLS";
                }
                Console.print(Colors.RED_BOLD+ "A process '"+name+"' has just stopped.");
            }
        }

        if(executorService != null){
            executorService.shutdown();
        }
        Console.print(ctx.channel().remoteAddress(),Level.FINE);
        IClient client = Client.getInstance().getClientManager().getClient(ctx);


        //Remove Server
        System.out.println("Remove server, client : " + client);
        if(client != null){
            client.getClientManager().getDevTools().remove(client);
            if(!client.isDevTool()){
                String server = client.getJvmService().getJvmExecutor().getName()+"-"+client.getJvmService().getId();
                for(IClient c : Client.getInstance().getClientManager().getClients().values()){
                    if(c.getJvmType() == JVMContainer.JVMType.SERVER){
                        c.getRequestManager().sendRequest(RequestType.SPIGOT_REMOVE_SERVERS,server);
                    }
                }
                for(IClient c : Client.getInstance().getClientManager().getDevTools()){
                    if(c != null){
                        //c.getRequestManager().sendRequest(RequestType.DEV_TOOLS_NEW_SERVER, server+";"+client.getJvmService().getJvmExecutor().getType()+";"+ client.getJvmService().getJvmExecutor().isProxy()+";false");
                        c.getRequestManager().sendRequest(RequestType.DEV_TOOLS_REMOVE_SERVERS,server);
                    }
                }
            }
        }
        //UNREGISTER CHANNEL
        if(client != null){
            Client.getInstance().getChannelManager().unregisterAllClientToChannel(client);
            //UNREGISTER PLAYER LISTENERS
            Client.getInstance().getServicePlayersManager().removeUpdatingClient(client);
        }




        //REMOVE SERVICES
        if(client != null && client.getJvmService() != null){
            client.getJvmService().getJvmExecutor().removeService(client.getJvmService().getId());
        }



        if(client != null && client.isDevTool()){
            ScreenManager screenManager = ScreenManager.instance;
            for(Screen screen : screenManager.getScreens().values()){
                screen.getDevToolsReading().remove(client);
            }
        }
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if(!Main.isDisabling()){
            if(!(cause instanceof java.net.SocketException)){
                cause.printStackTrace();
            }
        }
        ctx.close();
    }



    @Override
    public void writeAndFlush(Message msg, IClient client){
        this.writeAndFlush(msg,null,client);
    }

    @Override
    public void writeAndFlush(Message msg, GenericFutureListener<? extends Future<? super Void>> listener, IClient client){
        Console.print(Colors.YELLOW+"write and flush>> "+Colors.WHITE+ msg, Level.FINE);
        ChannelHandlerContext ctx = client.getChannelHandlerContext();
        Console.print(ctx, Level.FINE);
        if(ctx == null || !ctx.channel().isActive() || !queue.isEmpty()){
            assert ctx != null;
            queue.put(msg,new Tuple<>(ctx.channel(),listener));
            return;
        }
        byte[] entry = msg.toString().getBytes(StandardCharsets.UTF_8);
        /*final ByteBuf buf = ctx.alloc().buffer(entry.length);
        buf.writeInt(msg.toString().length());
        buf.writeBytes(entry);*/
        if(listener == null){
            ctx.writeAndFlush(entry);
            return;
        }

        ctx.writeAndFlush(entry).addListener(listener);
        //buf.release();
    }
}
