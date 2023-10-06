package be.alexandre01.dreamnetwork.api.connection.core.channels;

import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;

public interface IDNChannel {
    void received(AChannelPacket receivedPacket);

    void setData(String key, Object object, boolean autoSend, AServiceClient... clients);

    void storeData(String key, Object object, AServiceClient... clients);

    void storeData(String key, Object object, boolean autoSend, AServiceClient... clients);

    Object getData(String key);

    <T> T getData(String key, Class<T> clazz);

    void sendMessage(Message message, AServiceClient client);

    void addInterceptor(AChannelPacket.DNChannelInterceptor dnChannelInterceptor);

    String getName();

    java.util.HashMap<String, Object> getObjects();

    java.util.HashMap<String, Boolean> getAutoSendObjects();

    java.util.ArrayList<AChannelPacket.DNChannelInterceptor> getDnChannelInterceptors();
}
