package be.alexandre01.dreamnetwork.api.connection.core.communication;

import be.alexandre01.dreamnetwork.api.connection.core.handler.ICoreHandler;
import be.alexandre01.dreamnetwork.api.connection.core.request.IRequestManager;
import be.alexandre01.dreamnetwork.api.connection.core.request.Packet;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public interface IGlobalClient {
    String getName();
    Packet writeAndFlush(Message message);

    Packet writeAndFlush(Message message, GenericFutureListener<? extends Future<? super Void>> listener);
    io.netty.channel.ChannelHandlerContext getChannelHandlerContext();

    IRequestManager getRequestManager();

    ICoreHandler getCoreHandler();
}
