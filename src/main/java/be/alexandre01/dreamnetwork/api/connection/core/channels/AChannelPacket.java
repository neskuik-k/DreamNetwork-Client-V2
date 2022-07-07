package be.alexandre01.dreamnetwork.api.connection.core.channels;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IClient;
import be.alexandre01.dreamnetwork.client.connection.core.communication.Client;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public abstract class AChannelPacket {
    public abstract void createResponse(Message message, IClient client);

    public abstract void createResponse(Message message, IClient client, String header);

    public abstract void createResponse(Message message, IClient client, GenericFutureListener<? extends Future<? super Void>> listener);

    public abstract void createResponse(Message message, IClient client, GenericFutureListener<? extends Future<? super Void>> listener, String header);

    public interface DNChannelInterceptor{
        public void received(AChannelPacket receivedPacket);
    }
}
