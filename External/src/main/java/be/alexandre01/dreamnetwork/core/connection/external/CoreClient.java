package be.alexandre01.dreamnetwork.core.connection.external;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClientManager;
import be.alexandre01.dreamnetwork.api.connection.core.handler.ICoreHandler;
import be.alexandre01.dreamnetwork.api.connection.core.request.IRequestManager;
import be.alexandre01.dreamnetwork.api.connection.core.request.Packet;
import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.ArrayList;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 03/10/2023 at 23:15
*/
public class CoreClient implements IClient {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public Packet writeAndFlush(Message message) {
        return null;
    }

    @Override
    public Packet writeAndFlush(Message message, GenericFutureListener<? extends Future<? super Void>> listener) {
        return null;
    }

}
