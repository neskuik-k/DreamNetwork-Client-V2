package be.alexandre01.dreamnetwork.api.connection.core.communication;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

public interface IClientManager {
    AServiceClient registerClient(AServiceClient client);

    AServiceClient getClient(String processName);

    AServiceClient getClient(ChannelHandlerContext ctx);

    java.util.HashMap<String, AServiceClient> getClients();

    java.util.HashMap<ChannelHandlerContext, AServiceClient> getClientsByConnection();

    HashMap<String, AServiceClient> getExternalTools();

    AServiceClient getProxy();


}
