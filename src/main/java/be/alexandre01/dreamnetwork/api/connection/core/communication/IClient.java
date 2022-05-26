package be.alexandre01.dreamnetwork.api.connection.core.communication;

import be.alexandre01.dreamnetwork.api.connection.request.IRequestManager;
import be.alexandre01.dreamnetwork.client.connection.core.communication.Client;
import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public interface IClient {

    void writeAndFlush(Message message);

    void writeAndFlush(Message message, GenericFutureListener<? extends Future<? super Void>> listener);

    int getPort();

    boolean isDevTool();

    be.alexandre01.dreamnetwork.client.service.JVMContainer.JVMType getJvmType();

    String getInfo();

    io.netty.channel.ChannelHandlerContext getChannelHandlerContext();

    IRequestManager getRequestManager();

    be.alexandre01.dreamnetwork.client.connection.core.handler.CoreHandler getCoreHandler();

    be.alexandre01.dreamnetwork.client.service.JVMService getJvmService();

    ClientManager getClientManager();

    java.util.ArrayList<String> getAccessChannels();

    void setPort(int port);

    void setDevTool(boolean isDevTool);

    void setJvmType(be.alexandre01.dreamnetwork.client.service.JVMContainer.JVMType jvmType);

    void setInfo(String info);

    void setChannelHandlerContext(io.netty.channel.ChannelHandlerContext channelHandlerContext);

    void setCoreHandler(be.alexandre01.dreamnetwork.client.connection.core.handler.CoreHandler coreHandler);

    void setJvmService(be.alexandre01.dreamnetwork.client.service.JVMService jvmService);

    void setClientManager(ClientManager clientManager);

    void setAccessChannels(java.util.ArrayList<String> accessChannels);
}
