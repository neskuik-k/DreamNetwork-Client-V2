package be.alexandre01.dreamnetwork.core.connection.external.handler;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreResponse;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.connection.request.RequestType;
import be.alexandre01.dreamnetwork.core.connection.external.ExternalClient;
import be.alexandre01.dreamnetwork.core.connection.external.ExternalCore;
import be.alexandre01.dreamnetwork.core.connection.external.communication.BasicTransmission;
import be.alexandre01.dreamnetwork.core.connection.external.requests.ExtRequestManager;
import be.alexandre01.dreamnetwork.core.connection.external.requests.ExtResponse;
import be.alexandre01.dreamnetwork.core.connection.request.ClientRequestManager;
import be.alexandre01.dreamnetwork.core.utils.messages.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import lombok.Setter;

public class ExternalClientHandler extends ChannelInboundHandlerAdapter   {
    private ArrayList<ExtResponse> responses = new ArrayList<>();
    private HashMap<Message, GenericFutureListener<? extends Future<? super Void>>> queue = new HashMap<>();
    private ExternalClient externalClient;
    @Getter @Setter private Channel channel;

    public ExternalClientHandler(ExternalClient externalClient){
        this.externalClient = externalClient;
        responses.add(new BasicTransmission());
        System.out.println("Init external client handler");
        ExternalCore.getInstance().setClientHandler(this);
        ExternalCore.getInstance().init();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
       System.out.println("Channel registered");
        try {
            super.channelRegistered(ctx);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.channel = ctx.channel();
        System.out.println("Channel active");

        if(!queue.isEmpty()){
            taskQueue();
        }
        externalClient.trying = 0;

        ExternalCore.getInstance().setRequestManager(new ExtRequestManager(this));
        System.out.println("I'm sending handshake");

        ExternalCore.getInstance().getRequestManager().sendRequest(RequestType.CORE_HANDSHAKE);
    }

    private void taskQueue(){
        Message msg = (Message) queue.keySet().toArray()[0];
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
        if(queue.get(msg) != null){
            future.addListener(queue.get(msg));
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf m = (ByteBuf) msg; // (1)
        String s_to_decode = m.toString(StandardCharsets.UTF_8);

         //System.out.println("To_Decode >> "+ s_to_decode);

        //TO DECODE STRING IF ENCODED AS AES

        if(!Message.isJSONValid(s_to_decode))
            return;


        //System.out.println("TO message");

        try {
            Message message = Message.createFromJsonString(s_to_decode);
            if(!responses.isEmpty()){
                for(ExtResponse iResponse : responses){
                    try {
                        iResponse.onAutoResponse(message,ctx);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            System.out.println("Release");
            m.release();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Channel inactive try to reconnect...");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Channel unregister");
        if(ExternalCore.getInstance().isConnected())
            ExternalCore.getInstance().exitMode();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Handler added");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Handler removed");
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }






    public void writeAndFlush(Message msg){
        this.writeAndFlush(msg, null);
    }



    public void writeAndFlush(Message msg, GenericFutureListener<? extends Future<? super Void>> listener){
        if(channel == null || !channel.isActive() || !queue.isEmpty()){
            queue.put(msg,listener);
            return;
        }
        byte[] entry = msg.toString().getBytes(StandardCharsets.UTF_8);
        final ByteBuf buf = channel.alloc().buffer(entry.length);
        buf.writeBytes(entry);
        if(listener == null){
            channel.writeAndFlush(buf);
            return;
        }
        channel.writeAndFlush(buf).addListener(listener);
    }

}
