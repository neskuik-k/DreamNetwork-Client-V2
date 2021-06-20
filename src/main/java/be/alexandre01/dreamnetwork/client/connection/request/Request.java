package be.alexandre01.dreamnetwork.client.connection.request;


import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;

@Data
public class Request {
    private static int currentId;

    private final RequestType requestType;
    private final GenericFutureListener<? extends Future<? super Void>> listener;
    private final int RID;
    private final Message message;
    private String provider;
    private RequestFutureResponse requestFutureResponse;

    private ClientManager.Client client;

    public Request(RequestType requestType, Message message, GenericFutureListener<? extends Future<? super Void>> listener) {
        this.requestType = requestType;
        this.listener = listener;
        this.RID = currentId;
        this.message = message;
        this.provider = "core";
        message.setProvider(provider);
        message.put("RID",RID);
        currentId++;
    }

}
