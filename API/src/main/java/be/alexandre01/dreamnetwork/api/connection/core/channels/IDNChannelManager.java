package be.alexandre01.dreamnetwork.api.connection.core.channels;

import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;

public interface IDNChannelManager {
    AChannelPacket createChannelPacket(Message message);
    boolean hasChannel(String name);

    IDNChannel getChannel(String name);

    void createChannel(IDNChannel dnChannel);

    void registerClientToChannel(AServiceClient client, String channel, boolean resend);

    void unregisterClientToChannel(AServiceClient client, String channel);

    void unregisterAllClientToChannel(AServiceClient client);

    void registerCoreToChannel(String channel);

    void unregisterCoreToChannel(String channel);

    java.util.HashMap<String, IDNChannel> getChannels();

    com.google.common.collect.Multimap<String, AServiceClient> getClientsRegistered();

    java.util.ArrayList<AServiceClient> getDontResendsData();

    java.util.ArrayList<String> getChannelRegisteredInCore();

    boolean equals(Object o);

    int hashCode();

    String toString();
}
