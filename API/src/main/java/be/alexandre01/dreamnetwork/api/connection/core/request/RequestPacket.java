package be.alexandre01.dreamnetwork.api.connection.core.request;


import be.alexandre01.dreamnetwork.api.connection.core.communication.IGlobalClient;
import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;

@Data
public class RequestPacket implements Packet{
    private static int currentId;

    private final RequestInfo requestInfo;
    private final GenericFutureListener<? extends Future<? super Void>> listener;
    private final int messageID;
    private Message message;
    private String provider;
    private RequestFutureResponse requestFutureResponse;

    private AServiceClient client;

    public RequestPacket(RequestInfo requestInfo, Message message, GenericFutureListener<? extends Future<? super Void>> listener) {
        this.requestInfo = requestInfo;
        this.listener = listener;
        this.messageID = currentId;
        this.message = message;
        this.provider = "core";
        message.setProvider(provider);
        //message.setInRoot("MID",messageID);
        currentId++;
    }
    public RequestPacket(Message message, GenericFutureListener<? extends Future<? super Void>> listener) {
        this.requestInfo = RequestType.CUSTOM;
        this.listener = listener;
        this.messageID = currentId;
        this.message = message;
        this.provider = "core";
        message.setProvider(provider);
        //message.setInRoot("MID",messageID);
        currentId++;
    }
    public RequestPacket send(AServiceClient client){
        return client.getRequestManager().sendRequest(this);
    }

    @Override
    public IGlobalClient getReceiver() {
        return client;
    }
}
