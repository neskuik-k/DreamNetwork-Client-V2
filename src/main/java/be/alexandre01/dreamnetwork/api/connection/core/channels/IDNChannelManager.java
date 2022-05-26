package be.alexandre01.dreamnetwork.api.connection.core.channels;

import be.alexandre01.dreamnetwork.client.connection.core.channels.DNChannel;
import be.alexandre01.dreamnetwork.client.connection.core.communication.Client;

public interface IDNChannelManager {
    boolean hasChannel(String name);

    DNChannel getChannel(String name);

    void createChannel(DNChannel dnChannel);

    void registerClientToChannel(Client client, String channel, boolean resend);

    void unregisterClientToChannel(Client client, String channel);

    void unregisterAllClientToChannel(Client client);

    void registerCoreToChannel(String channel);

    void unregisterCoreToChannel(String channel);

    java.util.HashMap<String, DNChannel> getChannels();

    com.google.common.collect.Multimap<String, Client> getClientsRegistered();

    java.util.ArrayList<Client> getDontResendsData();

    java.util.ArrayList<String> getChannelRegisteredInCore();

    boolean equals(Object o);

    int hashCode();

    String toString();
}
