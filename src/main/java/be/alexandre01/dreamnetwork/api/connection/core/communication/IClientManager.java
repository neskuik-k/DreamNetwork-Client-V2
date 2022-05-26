package be.alexandre01.dreamnetwork.api.connection.core.communication;

import be.alexandre01.dreamnetwork.client.connection.core.communication.Client;
import io.netty.channel.ChannelHandlerContext;

public interface IClientManager {
    Client registerClient(Client client);

    Client getClient(String processName);

    Client getClient(ChannelHandlerContext ctx);

    java.util.HashMap<String, Client> getClients();

    java.util.HashMap<ChannelHandlerContext, Client> getClientsByConnection();

    java.util.ArrayList<Client> getDevTools();

    Client getProxy();

    void setProxy(Client proxy);
}
