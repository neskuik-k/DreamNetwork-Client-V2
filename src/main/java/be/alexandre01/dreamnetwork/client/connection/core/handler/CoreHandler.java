package be.alexandre01.dreamnetwork.client.connection.core.handler;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.Main;
import be.alexandre01.dreamnetwork.client.connection.core.communication.AuthentificationResponse;
import be.alexandre01.dreamnetwork.client.connection.core.communication.BaseResponse;
import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.client.connection.core.communication.CoreResponse;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import be.alexandre01.dreamnetwork.utils.Tuple;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class CoreHandler extends ChannelInboundHandlerAdapter{

    private ArrayList<CoreResponse> responses = new ArrayList<>();
    private boolean hasDevUtilSoftwareAccess = true;
    @Getter private ArrayList<ChannelHandlerContext> allowedCTX = new ArrayList<>();
    private AuthentificationResponse authResponse;
    //A PATCH
    private HashMap<Message, Tuple<Channel,GenericFutureListener<? extends Future<? super Void>>>> queue = new HashMap<>();
    private final Client client;
    public CoreHandler(){
        this.client = Client.getInstance();
        this.client.setCoreHandler(this);
        responses.add(new BaseResponse());
        responses.add(authResponse = new AuthentificationResponse());
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
        String s_to_decode = m.toString(StandardCharsets.UTF_8);
        //TO DECODE STRING IF ENCODED AS AES

        ChannelFuture closeFuture = ctx.channel().closeFuture();
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("Closed connection");
            }
        });
        if(!Message.isJSONValid(s_to_decode))
            return;

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
        if(allowedCTX.contains(ctx))
        Console.print("Déconnexion d'un serveur");

        if(executorService != null){
            executorService.shutdown();
        }
        Console.print(ctx.channel().remoteAddress(),Level.FINE);
        ClientManager.Client client = Client.getInstance().getClientManager().getClient(ctx);
        if(client != null){
            client.getClientManager().getDevTools().remove(client);
        }
        if(client != null && client.getJvmService() != null){
            client.getJvmService().getJvmExecutor().removeService(client.getJvmService().getId());
        }
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if(allowedCTX.contains(ctx))
        System.out.println("Déconnexion d'un serveur");
        ctx.close();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if(!Main.isDisabling()){
            cause.printStackTrace();
        }

        ctx.close();
    }

    public void writeAndFlush(Message msg, ClientManager.Client client){
        this.writeAndFlush(msg,null,client);
    }

    public void writeAndFlush(Message msg, GenericFutureListener<? extends Future<? super Void>> listener, ClientManager.Client client){
        Console.print(Colors.YELLOW+"write and flush>> "+Colors.WHITE+ msg, Level.FINE);
        ChannelHandlerContext ctx = client.getChannelHandlerContext();
        Console.print(ctx, Level.FINE);
        if(ctx == null || !ctx.channel().isActive() || !queue.isEmpty()){
            assert ctx != null;
            queue.put(msg,new Tuple<>(ctx.channel(),listener));
            return;
        }
        byte[] entry = msg.toString().getBytes(StandardCharsets.UTF_8);
        final ByteBuf buf = ctx.alloc().buffer(entry.length);
        buf.writeBytes(entry);
        if(listener == null){
            ctx.writeAndFlush(buf);
            return;
        }
        ctx.writeAndFlush(buf).addListener(listener);
    }
}
