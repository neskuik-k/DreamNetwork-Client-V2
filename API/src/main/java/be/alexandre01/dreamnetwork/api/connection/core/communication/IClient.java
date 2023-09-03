package be.alexandre01.dreamnetwork.api.connection.core.communication;

import be.alexandre01.dreamnetwork.api.connection.core.handler.ICoreHandler;
import be.alexandre01.dreamnetwork.api.connection.request.IRequestManager;
import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.api.service.IService;

import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public interface IClient {
    String getName();
    void writeAndFlush(Message message);

    void writeAndFlush(Message message, GenericFutureListener<? extends Future<? super Void>> listener);

    int getPort();

    boolean isExternalTool();

    boolean isExternalService();

    IContainer.JVMType getJvmType();

    String getInfo();

    io.netty.channel.ChannelHandlerContext getChannelHandlerContext();

    IRequestManager getRequestManager();

    ICoreHandler getCoreHandler();

    IService getJvmService();

    IClientManager getClientManager();

    java.util.ArrayList<String> getAccessChannels();

    void setPort(int port);

    void setExternalTool(boolean isExternal);

    void setExternalService(boolean isExternal);
    void setJvmType(IContainer.JVMType jvmType);

    void setInfo(String info);

    void setChannelHandlerContext(io.netty.channel.ChannelHandlerContext channelHandlerContext);

    void setCoreHandler(ICoreHandler coreHandler);



    void setClientManager(IClientManager clientManager);

    void setAccessChannels(java.util.ArrayList<String> accessChannels);
}
