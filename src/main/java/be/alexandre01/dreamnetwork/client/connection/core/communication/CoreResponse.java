package be.alexandre01.dreamnetwork.client.connection.core.communication;


import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;

public abstract class CoreResponse {
    public abstract void onResponse(Message message, ChannelHandlerContext ctx, ClientManager.Client client) throws Exception;

}
