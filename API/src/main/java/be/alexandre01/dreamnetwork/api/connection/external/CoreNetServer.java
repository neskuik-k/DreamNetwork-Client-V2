package be.alexandre01.dreamnetwork.api.connection.external;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.connection.core.communication.UniversalConnection;
import be.alexandre01.dreamnetwork.api.connection.core.request.IRequestManager;
import be.alexandre01.dreamnetwork.api.connection.core.request.Packet;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 03/10/2023 at 23:15
*/
public class CoreNetServer extends UniversalConnection {
    private final ChannelHandlerContext context;
    private final IRequestManager requestManager;

    public CoreNetServer(ChannelHandlerContext context, IRequestManager requestManager) {
        super(0, "external-core", context);
        this.context = context;
        this.requestManager = requestManager;
    }

    @Override
    public String getName() {
        return "core";
    }

    @Override
    public Packet writeAndFlush(Message message) {
        Packet packet = new Packet() {
            @Override
            public Message getMessage() {
                return message;
            }

            @Override
            public String getProvider() {
                return "external-core";
            }

            @Override
            public UniversalConnection getReceiver() {
                return CoreNetServer.this;
            }
        };
        DNCoreAPI.getInstance().getExternalCore().ifPresent(iExternalCore -> {
            iExternalCore.getClientHandler().writeAndFlush(message);
        });
        return packet;
    }

    @Override
    public Packet writeAndFlush(Message message, GenericFutureListener<? extends Future<? super Void>> listener) {
        Packet packet = new Packet() {
            @Override
            public Message getMessage() {
                return message;
            }

            @Override
            public String getProvider() {
                return "external-core";
            }

            @Override
            public UniversalConnection getReceiver() {
                return CoreNetServer.this;
            }
        };
        DNCoreAPI.getInstance().getExternalCore().ifPresent(iExternalCore -> {
            iExternalCore.getClientHandler().writeAndFlush(message, listener);
        });
        return packet;
    }

    @Override
    public ChannelHandlerContext getChannelHandlerContext() {
        return context;
    }

    @Override
    public IRequestManager getRequestManager() {
        return requestManager;
    }

}
