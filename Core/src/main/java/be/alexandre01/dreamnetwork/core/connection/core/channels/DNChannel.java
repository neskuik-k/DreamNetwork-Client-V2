package be.alexandre01.dreamnetwork.core.connection.core.channels;


import be.alexandre01.dreamnetwork.api.connection.core.channels.AChannelPacket;
import be.alexandre01.dreamnetwork.api.connection.core.channels.IDNChannel;
import be.alexandre01.dreamnetwork.api.connection.core.channels.IDNChannelManager;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import be.alexandre01.dreamnetwork.core.connection.core.communication.Client;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Getter
public class DNChannel implements IDNChannel {
    private String name;
    private HashMap<String, Object> objects = new HashMap<>();
    private HashMap<String, Boolean> autoSendObjects = new HashMap<>();
    private final ArrayList<AChannelPacket.DNChannelInterceptor> dnChannelInterceptors = new ArrayList<>();

    public DNChannel(String name){
        this.name = name;
    }


    @Override
    public void received(AChannelPacket receivedPacket){
        for(AChannelPacket.DNChannelInterceptor dnChannelInterceptor : dnChannelInterceptors){
            dnChannelInterceptor.received(receivedPacket);
        }
    }




    @Override
    public void setData(String key, Object object, boolean autoSend, IClient... clients){
        objects.put(key, object);
        autoSendObjects.put(key, autoSend);
    }

    @Override
    public void storeData(String key, Object object, IClient... clients){
        boolean autoSend = true;
        if(autoSendObjects.containsKey(key)){
            autoSend = autoSendObjects.get(key);
        }
        storeData(key,object,autoSend,clients);
    }

    @Override
    public void storeData(String key, Object object, boolean autoSend, IClient... clients){
        List<IClient> c = Arrays.asList(clients);
        setData(key,object,autoSend);
        Console.printLang("connection.core.channels.object", object);
        if(autoSend){
            ChannelPacket channelPacket = new ChannelPacket(getName(),"core");
            Message message;
            message = new Message().set("key", key).set("value", object);
            IDNChannelManager channelManager = Core.getInstance().getChannelManager();
            if(channelManager.getClientsRegistered().isEmpty())
                return;


            for(IClient client : channelManager.getClientsRegistered().get(getName())){
                if(c.contains(client)){
                    continue;
                }
                 channelPacket.createResponse(message,client,"cData");
            }
        }
    }


    @Override
    public Object getData(String key){
        return objects.get(key);
    }


    @Override
    public <T> T getData(String key, Class<T> clazz){
        return objects.get(key) == null ? null : (T) objects.get(key);
    }

    @Override
    public void sendMessage(Message message, IClient client){
        message.setProvider("core");
        ChannelPacket channelPacket = new ChannelPacket(getName(),"core");
        channelPacket.createResponse(message,client);
    }

    @Override
    public void addInterceptor(AChannelPacket.DNChannelInterceptor dnChannelInterceptor){
        dnChannelInterceptors.add(dnChannelInterceptor);
    }

}
