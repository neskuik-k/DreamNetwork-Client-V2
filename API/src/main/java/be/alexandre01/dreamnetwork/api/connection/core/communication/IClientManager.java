package be.alexandre01.dreamnetwork.api.connection.core.communication;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

public interface IClientManager {
    IClient registerClient(IClient client);

    IClient getClient(String processName);

    IClient getClient(ChannelHandlerContext ctx);

    java.util.HashMap<String, IClient> getClients();

    java.util.HashMap<ChannelHandlerContext, IClient> getClientsByConnection();

    HashMap<String,IClient> getExternalTools();

    IClient getProxy();


}
