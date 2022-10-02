package be.alexandre01.dreamnetwork.api.connection.request;


import be.alexandre01.dreamnetwork.core.connection.core.communication.Client;
import be.alexandre01.dreamnetwork.core.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;

@Data
public class RequestPacket {
    private static int currentId;

    private final RequestInfo requestInfo;
    private final GenericFutureListener<? extends Future<? super Void>> listener;
    private final int requestID;
    private Message message;
    private String provider;
    private RequestFutureResponse requestFutureResponse;

    private Client client;

    public RequestPacket(RequestInfo requestInfo, Message message, GenericFutureListener<? extends Future<? super Void>> listener) {
        this.requestInfo = requestInfo;
        this.listener = listener;
        this.requestID = currentId;
        this.message = message;
        this.provider = "core";
        message.setProvider(provider);
        message.setInRoot("MID",requestID);
        currentId++;
    }
    public RequestPacket(Message message, GenericFutureListener<? extends Future<? super Void>> listener) {
        this.requestInfo = RequestType.CUSTOM;
        this.listener = listener;
        this.requestID = currentId;
        this.message = message;
        this.provider = "core";
        message.setProvider(provider);
        message.setInRoot("MID",requestID);
        currentId++;
    }
    public RequestPacket send(Client client){
        return client.getRequestManager().sendRequest(this);
    }
}
