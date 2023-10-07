package be.alexandre01.dreamnetwork.api.connection.core.communication;

import be.alexandre01.dreamnetwork.api.connection.external.ExternalClient;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

public interface IClientManager {
    AServiceClient registerClient(AServiceClient client);

    <T> T getClient(ChannelHandlerContext ctx, Class<T> tClass);

    UniversalConnection getClient(ChannelHandlerContext ctx);

    AServiceClient getServiceClient(ChannelHandlerContext ctx);

    public AServiceClient getClient(String processName);


    java.util.HashMap<String, AServiceClient> getServiceClients();

    java.util.HashMap<ChannelHandlerContext, UniversalConnection> getClientsByConnection();

    HashMap<String, ExternalClient> getExternalTools();

    AServiceClient getProxy();


}
