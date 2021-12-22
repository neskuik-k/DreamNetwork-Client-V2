package be.alexandre01.dreamnetwork.client.connection.core.channels;


import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Getter
public class DNChannel {
    private String name;
    private HashMap<String, Object> objects = new HashMap<>();
    private HashMap<String, Boolean> autoSendObjects = new HashMap<>();
    private final ArrayList<DNChannelInterceptor> dnChannelInterceptors = new ArrayList<>();

    public DNChannel(String name){
        this.name = name;
    }

    public void received(ChannelPacket receivedPacket){
        for(DNChannelInterceptor dnChannelInterceptor : dnChannelInterceptors){
            dnChannelInterceptor.received(receivedPacket);
        }
    }

    public void setData(String key, Object object, boolean autoSend, ClientManager.Client... clients){
        objects.put(key, object);
        autoSendObjects.put(key, autoSend);
    }
    public void storeData(String key, Object object, ClientManager.Client... clients){
        boolean autoSend = true;
        if(autoSendObjects.containsKey(key)){
            autoSend = autoSendObjects.get(key);
        }
        storeData(key,object,autoSend,clients);
    }
    public void storeData(String key, Object object, boolean autoSend, ClientManager.Client... clients){
        List<ClientManager.Client> c = Arrays.asList(clients);
        setData(key,object,autoSend);
        System.out.println("Object>>"+ object);
        if(autoSend){
            ChannelPacket channelPacket = new ChannelPacket(getName(),"core");
            Message message;
            message = new Message().set("key", key).set("value", object);
            DNChannelManager channelManager = Client.getInstance().getChannelManager();
            if(channelManager.getClientsRegistered().isEmpty())
                return;

            for(ClientManager.Client client : channelManager.getClientsRegistered().get(getName())){
                if(c.contains(client)){
                    continue;
                }
                 channelPacket.createResponse(message,client,"cData");
            }
        }
    }

    public Object getData(String key){
        return objects.get(key);
    }

    public <T> T getData(String key, Class<T> clazz){
        return objects.get(key) == null ? null : (T) objects.get(key);
    }
    public void sendMessage(Message message, ClientManager.Client client){
        message.setProvider("core");
        ChannelPacket channelPacket = new ChannelPacket(getName(),"core");
        channelPacket.createResponse(message,client);
    }
    public void addInterceptor(DNChannelInterceptor dnChannelInterceptor){
        dnChannelInterceptors.add(dnChannelInterceptor);
    }
    public interface DNChannelInterceptor{
        public void received(ChannelPacket receivedPacket);
    }
}
