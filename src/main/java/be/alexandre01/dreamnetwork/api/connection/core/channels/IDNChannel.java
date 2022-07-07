package be.alexandre01.dreamnetwork.api.connection.core.channels;

import be.alexandre01.dreamnetwork.api.connection.core.channels.AChannelPacket;
import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;

public interface IDNChannel {
    void received(AChannelPacket receivedPacket);

    void setData(String key, Object object, boolean autoSend, be.alexandre01.dreamnetwork.client.connection.core.communication.Client... clients);

    void storeData(String key, Object object, IClient... clients);

    void storeData(String key, Object object, boolean autoSend, IClient... clients);

    Object getData(String key);

    <T> T getData(String key, Class<T> clazz);

    void sendMessage(Message message, IClient client);

    void addInterceptor(AChannelPacket.DNChannelInterceptor dnChannelInterceptor);

    String getName();

    java.util.HashMap<String, Object> getObjects();

    java.util.HashMap<String, Boolean> getAutoSendObjects();

    java.util.ArrayList<AChannelPacket.DNChannelInterceptor> getDnChannelInterceptors();
}
