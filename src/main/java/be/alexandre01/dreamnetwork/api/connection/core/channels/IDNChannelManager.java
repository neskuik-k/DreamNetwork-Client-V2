package be.alexandre01.dreamnetwork.api.connection.core.channels;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.client.connection.core.channels.DNChannel;
import be.alexandre01.dreamnetwork.client.connection.core.communication.Client;

public interface IDNChannelManager {
    boolean hasChannel(String name);

    IDNChannel getChannel(String name);

    void createChannel(IDNChannel dnChannel);

    void registerClientToChannel(IClient client, String channel, boolean resend);

    void unregisterClientToChannel(IClient client, String channel);

    void unregisterAllClientToChannel(IClient client);

    void registerCoreToChannel(String channel);

    void unregisterCoreToChannel(String channel);

    java.util.HashMap<String, IDNChannel> getChannels();

    com.google.common.collect.Multimap<String, IClient> getClientsRegistered();

    java.util.ArrayList<IClient> getDontResendsData();

    java.util.ArrayList<String> getChannelRegisteredInCore();

    boolean equals(Object o);

    int hashCode();

    String toString();
}
