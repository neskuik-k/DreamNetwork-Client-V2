package be.alexandre01.dreamnetwork.api.connection.external.handler;

import be.alexandre01.dreamnetwork.api.connection.core.handler.ICallbackManager;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 07/10/2023 at 14:42
*/
public interface IExternalClientHandler extends ChannelHandler, ChannelInboundHandler {
    @Override
    void channelRegistered(ChannelHandlerContext ctx);

    @Override
    void channelActive(ChannelHandlerContext ctx);

    @Override
    void channelRead(ChannelHandlerContext ctx, Object msg);

    @Override
    void channelReadComplete(ChannelHandlerContext ctx) throws Exception;

    @Override
    void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception;

    @Override
    void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception;

    @Override
    void channelInactive(ChannelHandlerContext ctx) throws Exception;

    @Override
    void channelUnregistered(ChannelHandlerContext ctx) throws Exception;

    @Override
    void handlerAdded(ChannelHandlerContext ctx) throws Exception;

    @Override
    void handlerRemoved(ChannelHandlerContext ctx) throws Exception;

    @Override
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause);

    void writeAndFlush(Message msg);

    void writeAndFlush(Message msg, GenericFutureListener<? extends Future<? super Void>> listener);

    ICallbackManager getCallbackManager();

    io.netty.channel.Channel getChannel();

    void setChannel(io.netty.channel.Channel channel);
}
