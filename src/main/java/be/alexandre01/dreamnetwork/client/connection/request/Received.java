package be.alexandre01.dreamnetwork.client.connection.request;

import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class Received {
    private static int currentId;

    private final RequestType requestType;
    private final GenericFutureListener<? extends Future<? super Void>> listener;
    private final int RID;
    private final Message message;
    private String provider;
    private RequestFutureResponse requestFutureResponse;

    private ClientManager.Client client;

    public Received(Message message){
        this.message = message;
        this.requestType = message.getRequest();
        this.listener = null;
        this.RID = Integer.parseInt((String) message.get("RID"));
        this.provider = message.getProvider();
    }


    public void createResponse(Message message, ClientManager.Client client){
        message.setProvider(provider);
        client.writeAndFlush(message);
    }

    public void createResponse(Message message, ClientManager.Client client,GenericFutureListener<? extends Future<? super Void>> listener){
        message.setProvider(provider);
        client.writeAndFlush(message,listener);
    }
}
