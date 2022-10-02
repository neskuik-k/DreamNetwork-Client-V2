package be.alexandre01.dreamnetwork.core.connection.core.channels;

import be.alexandre01.dreamnetwork.api.connection.core.channels.IDNChannel;
import be.alexandre01.dreamnetwork.api.connection.core.channels.IDNChannelManager;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.connection.request.RequestType;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;

@Data
public class DNChannelManager implements IDNChannelManager {
    final HashMap<String, IDNChannel> channels;
    public final Multimap<String, IClient> clientsRegistered = ArrayListMultimap.create();
    public final ArrayList<IClient> dontResendsData = new ArrayList<>();
    final ArrayList<String> channelRegisteredInCore = new ArrayList<>();
    public DNChannelManager(){
        channels = new HashMap<>();
    }

    @Override
    public boolean hasChannel(String name){
        return channels.containsKey(name);
    }

    @Override
    public IDNChannel getChannel(String name){
        return channels.get(name);
    }

    @Override
    public void createChannel(IDNChannel dnChannel){
        channels.put(dnChannel.getName(),dnChannel);
        Console.print(Colors.PURPLE + "Le channel " + dnChannel.getName() + " s'est enregistré " + Colors.RESET);
    }


    @Override
    public void registerClientToChannel(IClient client, String channel, boolean resend){
        clientsRegistered.put(channel,client);
        if(!hasChannel(channel)){
            createChannel(new DNChannel(channel));
        }else {
            getChannel(channel);
        }

        client.getRequestManager().sendRequest(RequestType.CORE_REGISTER_CHANNEL,channel,getChannel(channel).getObjects());
        if(!resend)
            dontResendsData.add(client);

        client.getAccessChannels().add(channel);
    }


    @Override
    public void unregisterClientToChannel(IClient client, String channel){
        client.getAccessChannels().remove(channel);

        clientsRegistered.remove(channel,client);
    }


    @Override
    public void unregisterAllClientToChannel(IClient client){
        if(!client.getAccessChannels().isEmpty()){
            for (String channel : client.getAccessChannels()){
                clientsRegistered.remove(channel,client);
            }
        }
        client.getAccessChannels().clear();

    }

    @Override
    public void registerCoreToChannel(String channel){
        channelRegisteredInCore.add(channel);
    }


    @Override
    public void unregisterCoreToChannel(String channel){
        channelRegisteredInCore.remove(channel);
    }
}
