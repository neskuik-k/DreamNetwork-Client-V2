package be.alexandre01.dreamnetwork.api.connection.core.communication;

import be.alexandre01.dreamnetwork.api.connection.core.handler.ICoreHandler;
import be.alexandre01.dreamnetwork.api.connection.core.request.IRequestManager;
import be.alexandre01.dreamnetwork.api.connection.core.request.Packet;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.util.Pack;

import java.util.ArrayList;

@Getter
@Setter
public abstract class UniversalConnection {
    private String name;
    private int port;
    private String info;
    private ChannelHandlerContext channelHandlerContext;

    private IRequestManager requestManager;
    private IClientManager clientManager;
    private ArrayList<String> accessChannels = new ArrayList<>();


    public UniversalConnection(int port, String info, ChannelHandlerContext ctx) {
        this.port = port;
        this.info = info;
        this.channelHandlerContext = ctx;
        this.name = "Unknown-Client="+ ctx.channel().remoteAddress().toString()+":"+port;
    }

    @Deprecated
    public Packet writeAndFlush(Message message) {
        getCoreHandler().writeAndFlush(message, this);
        return message.toPacket(this);
    }

    @Deprecated
    public Packet writeAndFlush(Message message, GenericFutureListener<? extends Future<? super Void>> listener) {
        getCoreHandler().writeAndFlush(message, listener, this);
        return message.toPacket(this);
    }

    public Packet dispatch(Packet packet) {
        getCoreHandler().writeAndFlush(packet.getMessage(), this);
        return packet;
    }

    public Packet dispatch(Packet packet, GenericFutureListener<? extends Future<? super Void>> future) {
        getCoreHandler().writeAndFlush(packet.getMessage(),future, this);
        return packet;
    }
    public abstract ICoreHandler getCoreHandler();
}
