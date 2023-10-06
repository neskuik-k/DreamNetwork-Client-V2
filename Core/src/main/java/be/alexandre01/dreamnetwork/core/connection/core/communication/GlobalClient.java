package be.alexandre01.dreamnetwork.core.connection.core.communication;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClientManager;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IGlobalClient;
import be.alexandre01.dreamnetwork.api.connection.core.handler.ICoreHandler;
import be.alexandre01.dreamnetwork.api.connection.core.request.Packet;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import be.alexandre01.dreamnetwork.core.connection.core.handler.CoreHandler;
import be.alexandre01.dreamnetwork.core.connection.core.requests.ClientRequestManager;
import be.alexandre01.dreamnetwork.core.service.JVMContainer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class GlobalClient implements IGlobalClient {
    private String name;
    private int port;
    private JVMContainer.JVMType jvmType = null;
    private String info;
    private ChannelHandlerContext channelHandlerContext;

    private ClientRequestManager requestManager;
    private ICoreHandler coreHandler;
    private IClientManager clientManager;
    private ArrayList<String> accessChannels = new ArrayList<>();



    @Builder
    public GlobalClient(int port, String info, CoreHandler coreHandler, ChannelHandlerContext ctx, JVMContainer.JVMType jvmType, boolean isExternalTool, boolean isExternalService) {
        this.port = port;
        this.info = info;
        this.coreHandler = coreHandler;
        this.channelHandlerContext = ctx;
        this.jvmType = jvmType;
        this.name = "Unknown-Client="+ ctx.channel().remoteAddress().toString()+":"+port;
    }

    @Override
    public Packet writeAndFlush(Message message) {
        coreHandler.writeAndFlush(message, this);
        return message.toPacket(this);
    }


    @Override
    public Packet writeAndFlush(Message message, GenericFutureListener<? extends Future<? super Void>> listener) {
        coreHandler.writeAndFlush(message, listener, this);
        return message.toPacket(this);
    }
}
