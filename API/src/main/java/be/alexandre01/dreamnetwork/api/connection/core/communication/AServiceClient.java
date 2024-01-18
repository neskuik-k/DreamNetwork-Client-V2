package be.alexandre01.dreamnetwork.api.connection.core.communication;

import be.alexandre01.dreamnetwork.api.connection.core.handler.ICoreHandler;
import be.alexandre01.dreamnetwork.api.connection.core.request.IRequestManager;
import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.api.service.IService;
import io.netty.channel.ChannelHandlerContext;

public abstract class AServiceClient extends UniversalConnection {
    public AServiceClient(int port, String info, ChannelHandlerContext ctx) {
        super(port, info, ctx);
    }

    public abstract String getName();
    public abstract int getPort();
    public abstract boolean isExternalTool();

    public abstract boolean isExternalService();

    public abstract IContainer.JVMType getJvmType();

    public abstract String getInfo();

    public abstract io.netty.channel.ChannelHandlerContext getChannelHandlerContext();

    public abstract IRequestManager getRequestManager();

    public abstract ICoreHandler getCoreHandler();

    public abstract IService getService();

    public abstract IClientManager getClientManager();

    public abstract java.util.ArrayList<String> getAccessChannels();

    public abstract void setPort(int port);

    public abstract void setExternalTool(boolean isExternal);

    public abstract void setExternalService(boolean isExternal);
    public abstract void setJvmType(IContainer.JVMType jvmType);

    public abstract void setInfo(String info);

    public abstract void setChannelHandlerContext(io.netty.channel.ChannelHandlerContext channelHandlerContext);

    public abstract void setCoreHandler(ICoreHandler coreHandler);


    public abstract void setClientManager(IClientManager clientManager);

    public abstract void setAccessChannels(java.util.ArrayList<String> accessChannels);
}
