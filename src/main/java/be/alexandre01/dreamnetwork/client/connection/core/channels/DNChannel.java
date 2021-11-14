package be.alexandre01.dreamnetwork.client.connection.core.channels;


import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public abstract class DNChannel {
    private String name;
    private final ArrayList<DNChannelInterceptor> dnChannelInterceptors = new ArrayList<>();

    public DNChannel(String name){
        this.name = name;
    }

    public abstract void received(ChannelPacket receivedPacket);

    public void sendMessage(Message message, ClientManager.Client client){
        message.setProvider("core");
        ChannelPacket channelPacket = new ChannelPacket(message);
        channelPacket.createResponse(message,client);
    }
    public void addInterceptor(DNChannelInterceptor dnChannelInterceptor){
        dnChannelInterceptors.add(dnChannelInterceptor);
    }
    public interface DNChannelInterceptor{
        public void received(ChannelPacket receivedPacket);
    }
}
