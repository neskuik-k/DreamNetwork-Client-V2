package be.alexandre01.dreamnetwork.core.connection.core.requests;


import be.alexandre01.dreamnetwork.api.connection.core.communication.UniversalConnection;
import be.alexandre01.dreamnetwork.api.connection.core.handler.ICoreHandler;
import be.alexandre01.dreamnetwork.api.connection.core.request.*;
import be.alexandre01.dreamnetwork.core.connection.core.communication.ServiceClient;
import be.alexandre01.dreamnetwork.api.connection.core.request.exceptions.RequestNotFoundException;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;

import java.util.Collection;

public class ClientRequestManager extends AbstractRequestManager {
    @Getter
    RequestBuilder requestBuilder;
    private UniversalConnection client;
    private ChannelHandlerContext ctx;
    private ICoreHandler handler;





    public ClientRequestManager(UniversalConnection client){
        super();
        this.client = client;
        this.ctx = client.getChannelHandlerContext();
        this.handler = client.getCoreHandler();
        requestBuilder = new RequestBuilder();
      //  requestBuilder.addRequestBuilder();
    }



    /*public RequestPacket sendRequest(RequestPacket request,Object... args){
        Collection<RequestBuilder.RequestData> requestData = requestBuilder.getRequestData().get(request.getRequestInfo());
        if(client != null)
         request.setClient(client);
        Message message = request.getMessage();
        if(requestData != null) {
            for (RequestBuilder.RequestData data : requestData) {
                message = data.write(message, client, args);
            }
        }
        if(RequestBuilder.getGlobalRequestData().containsKey(request.getRequestInfo())){
            for(RequestBuilder.RequestData data : RequestBuilder.getGlobalRequestData().get(request.getRequestInfo())){
                message = data.write(message,client,args);
            }
        }
        request.setMessage(message);
        handler.writeAndFlush(request.getMessage(),request.getListener(),client);
        requests.put(request.getRequestID(),request);
        return request;
    }*/
    @Override
    public RequestPacket sendRequest(RequestInfo requestInfo, Message message, GenericFutureListener<? extends Future<? super Void>> listener, Object... args){
        RequestPacket request = getRequest(requestInfo,message,listener,args);
        request.getClient().writeAndFlush(request.getMessage(),listener);
        //requests.put(request.getRequestID(),request);
        return request;
         //client.writeAndFlush(requestData.write(message,client,args),listener);
    }

    @Override
    public RequestPacket getRequest(RequestInfo requestInfo, Message message,GenericFutureListener<? extends Future<? super Void>> listener, Object... args){
        if(!requestBuilder.getRequestData().containsKey(requestInfo)){
            try {
                throw new RequestNotFoundException(requestInfo,client);
            } catch (RequestNotFoundException e) {
                e.printStackTrace();
            }
        }
        Collection<RequestBuilder.RequestData> requestData = requestBuilder.getRequestData().get(requestInfo);

        message.setHeader("RI");
        message.setRequestInfo(requestInfo);
        if(requestData != null) {
            for (RequestBuilder.RequestData data : requestData) {
                message = data.write(message, client, args);
            }
        }
        if(RequestBuilder.getGlobalRequestData().containsKey(requestInfo)){
            for(RequestBuilder.RequestData data : RequestBuilder.getGlobalRequestData().get(requestInfo)){
                message = data.write(message,client,args);
            }
        }
        RequestPacket request = new RequestPacket(requestInfo,message,listener);
        request.setClient(client);
        return request;
    }

}
