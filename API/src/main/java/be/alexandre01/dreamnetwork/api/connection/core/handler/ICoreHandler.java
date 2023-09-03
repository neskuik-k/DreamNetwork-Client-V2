package be.alexandre01.dreamnetwork.api.connection.core.handler;

import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreResponse;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.ArrayList;

public interface ICoreHandler extends ChannelHandler, ChannelInboundHandler {
    @Override
    void channelRegistered(ChannelHandlerContext ctx);

    @Override
    void channelActive(ChannelHandlerContext ctx);

    @Override
    void channelRead(ChannelHandlerContext ctx, Object msg);

    @Override
    void channelUnregistered(ChannelHandlerContext ctx) throws Exception;

    @Override
    void handlerRemoved(ChannelHandlerContext ctx) throws Exception;

    @Override
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause);

    void writeAndFlush(Message msg, IClient client);

    void writeAndFlush(Message msg, GenericFutureListener<? extends Future<? super Void>> listener, IClient client);

    boolean isHasDevUtilSoftwareAccess();

    ArrayList<ChannelHandlerContext> getAllowedCTX();

    ArrayList<ChannelHandlerContext> getExternalConnections();

    void setHasDevUtilSoftwareAccess(boolean hasDevUtilSoftwareAccess);

    public ArrayList<CoreResponse> getResponses();
}
