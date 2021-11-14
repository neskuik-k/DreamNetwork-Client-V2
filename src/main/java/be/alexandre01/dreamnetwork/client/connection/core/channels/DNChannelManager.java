package be.alexandre01.dreamnetwork.client.connection.core.channels;

import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;

@Data
public class DNChannelManager {
    final HashMap<String, DNChannel> channels;
    public final Multimap<String, ClientManager.Client> clientsRegistered = ArrayListMultimap.create();
    final ArrayList<String> channelRegisteredInCore = new ArrayList<>();
    public DNChannelManager(){
        channels = new HashMap<>();
    }
    public boolean hasChannel(String name){
        return channels.containsKey(name);
    }
    public DNChannel getChannel(String name){
        return channels.get(name);
    }
    public void createChannel(DNChannel dnChannel){
        channels.put(dnChannel.getName(),dnChannel);
    }

    public void registerClientToChannel(ClientManager.Client client, String channel){
        clientsRegistered.put(channel,client);
        client.getAccessChannels().add(channel);
    }

    public void unregisterClientToChannel(ClientManager.Client client, String channel){
        client.getAccessChannels().remove(channel);

        clientsRegistered.remove(channel,client);
    }

    public void unregisterAllClientToChannel(ClientManager.Client client){
        if(!client.getAccessChannels().isEmpty()){
            for (String channel : client.getAccessChannels()){
                clientsRegistered.remove(channel,client);
            }
        }
        client.getAccessChannels().clear();

    }
    public void registerCoreToChannel(String channel){
        channelRegisteredInCore.add(channel);
    }

    public void unregisterCoreToChannel(String channel){
        channelRegisteredInCore.remove(channel);
    }
}
