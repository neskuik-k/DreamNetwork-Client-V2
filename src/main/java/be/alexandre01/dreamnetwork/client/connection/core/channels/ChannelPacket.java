package be.alexandre01.dreamnetwork.client.connection.core.channels;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.client.connection.request.RequestBuilder;
import be.alexandre01.dreamnetwork.client.connection.request.RequestFutureResponse;
import be.alexandre01.dreamnetwork.client.connection.request.RequestType;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

@SuppressWarnings("unused")
public class ChannelPacket {
    private static int currentId;

    private RequestType requestType;
    private final GenericFutureListener<? extends Future<? super Void>> listener;
    private Integer RID = null;
    private final Message message;
    private final String provider;
    private RequestFutureResponse requestFutureResponse;

    private ClientManager.Client client;
    private final String channel;

    public ChannelPacket(Message message){
        this.message = message;
        if(message.hasRequest())
         this.requestType = message.getRequest();
        this.listener = null;
        if(message.containsKey("RID")){
            this.RID = message.getRequestID();
        }
        this.provider = message.getProvider();
        this.channel = message.getChannel();
    }
    public ChannelPacket(String channel,String provider){
        this.listener = null;
        this.provider = provider;
        this.channel = channel;
        this.message = null;
    }

    public void createResponse(Message message,ClientManager.Client client){
        createResponse(message,client,null,"channel");
    }
    public void createResponse(Message message,ClientManager.Client client,String header){
        createResponse(message,client,null,header);
    }

    public void createResponse(Message message,ClientManager.Client client, GenericFutureListener<? extends Future<? super Void>> listener){
        createResponse(message,client,listener,"channel");
    }

    public void createResponse(Message message,ClientManager.Client client, GenericFutureListener<? extends Future<? super Void>> listener,String header){
        message.setProvider(provider);
        message.setSender("core");
        message.setHeader(header);
        message.setChannel(this.channel);

        if(requestType != null){
            RequestBuilder.RequestData requestData = client.getRequestManager().getRequestBuilder().getRequestData().get(requestType);
            if(requestData != null)
            message = requestData.write(message,client,this.provider);
        }


        if(RID != null)
            message.setInRoot("RID",RID);
        client.writeAndFlush(message,listener);
    }

}
