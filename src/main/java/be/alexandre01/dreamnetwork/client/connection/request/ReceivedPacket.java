package be.alexandre01.dreamnetwork.client.connection.request;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

@SuppressWarnings("unused")
public class ReceivedPacket {
    private static int currentId;

    private RequestType requestType;
    private final GenericFutureListener<? extends Future<? super Void>> listener;
    private int RID = -1;
    private final Message message;
    private String provider;
    private RequestFutureResponse requestFutureResponse;

    private ClientManager.Client client;
    private String channel;

    public ReceivedPacket(Message message){
        this.message = message;
        if(message.hasRequest())
         this.requestType = message.getRequest();
        this.listener = null;
        if(message.containsKey("RID")){
            this.RID = Integer.parseInt((String) message.get("RID"));
        }
        this.provider = message.getProvider();
        this.channel = message.getChannel();
    }

    public void createResponse(Message message, ClientManager.Client client){
        createResponse(message,client,null);
    }

    public void createResponse(Message message, ClientManager.Client client,GenericFutureListener<? extends Future<? super Void>> listener){

        message.setProvider(provider);
        message.setSender("core");
        message.setRequestType(RequestType.CORE_RETRANSMISSION);
        message.setChannel(this.channel);
        RequestBuilder.RequestData requestData = client.getRequestManager().requestBuilder.requestData.get(requestType);

        message = requestData.write(message,client,this.provider);

        if(RID != -1)
            message.put("RID",""+RID);
        client.writeAndFlush(message,listener);
    }
}
