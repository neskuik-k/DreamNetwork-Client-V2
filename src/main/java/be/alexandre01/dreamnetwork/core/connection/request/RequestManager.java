package be.alexandre01.dreamnetwork.core.connection.request;


import be.alexandre01.dreamnetwork.api.connection.request.*;
import be.alexandre01.dreamnetwork.core.connection.core.communication.Client;
import be.alexandre01.dreamnetwork.core.connection.request.exception.RequestNotFoundException;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;

public class RequestManager implements IRequestManager {
    @Getter
    RequestBuilder requestBuilder;
    private Client client;
    private HashMap<Integer, RequestPacket> requests = new HashMap<>();

    public RequestManager(Client client){
        this.client = client;
        requestBuilder = new RequestBuilder();
        requestBuilder.addRequestBuilder();
    }

    public RequestPacket sendRequest(RequestPacket request,Object... args){
        Collection<RequestBuilder.RequestData> requestData = requestBuilder.getRequestData().get(request.getRequestInfo());

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
        request.getClient().writeAndFlush(request.getMessage(),request.getListener());
        requests.put(request.getRequestID(),request);
        return request;
    }
    public RequestPacket sendRequest(RequestInfo requestInfo, Message message, GenericFutureListener<? extends Future<? super Void>> listener, Object... args){
         if(!requestBuilder.getRequestData().containsKey(requestInfo)){
             try {
                 throw new RequestNotFoundException(requestInfo);
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
        request.getClient().writeAndFlush(request.getMessage(),listener);
        requests.put(request.getRequestID(),request);
        return request;
         //client.writeAndFlush(requestData.write(message,client,args),listener);
    }

    public RequestPacket sendRequest(RequestInfo requestInfo, Object... args){
       return this.sendRequest(requestInfo,new Message(),future -> {
           Console.printLang("connection.request.sent", Level.FINE, requestInfo.name());
       },args);
    }

    public RequestPacket sendRequest(RequestInfo requestInfo, Message message, Object... args){
        return this.sendRequest(requestInfo,message,null,args);
    }

    public RequestPacket sendRequest(RequestInfo requestInfo, boolean notifiedWhenSent, Object... args){
        if(notifiedWhenSent){
          return this.sendRequest(requestInfo,new Message(),future -> {
              Console.printLang("connection.request.sent", requestInfo.name());
            },args);

        }
       return this.sendRequest(requestInfo,new Message(),null,args);
    }

    public RequestPacket sendRequest(RequestInfo requestInfo, Message message, boolean notifiedWhenSent, Object... args){
        if(notifiedWhenSent){
            return this.sendRequest(requestInfo,message,future -> {
                Console.printLang("connection.request.sent", requestInfo.name());
            },args);

        }
        return this.sendRequest(requestInfo,message,null,args);
    }
    public RequestPacket getRequest(int MID){
        return requests.get(MID);
    }
}
