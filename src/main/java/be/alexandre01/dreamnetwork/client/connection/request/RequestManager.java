package be.alexandre01.dreamnetwork.client.connection.request;


import be.alexandre01.dreamnetwork.api.connection.request.*;
import be.alexandre01.dreamnetwork.client.connection.core.communication.Client;
import be.alexandre01.dreamnetwork.client.connection.request.exception.RequestNotFoundException;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;

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
        RequestBuilder.RequestData requestData = requestBuilder.getRequestData().get(request.getRequestInfo());
        request.setClient(client);
        request.setMessage(requestData.write(request.getMessage(),client,args));
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

         RequestBuilder.RequestData requestData = requestBuilder.getRequestData().get(requestInfo);
         message.setHeader("RequestType");
        message.setRequestInfo(requestInfo);
        RequestPacket request = new RequestPacket(requestInfo,requestData.write(message,client,args),listener);
        request.setClient(client);
        request.getClient().writeAndFlush(request.getMessage(),listener);
        requests.put(request.getRequestID(),request);
        return request;
         //client.writeAndFlush(requestData.write(message,client,args),listener);
    }

    public RequestPacket sendRequest(RequestInfo requestInfo, Object... args){
       return this.sendRequest(requestInfo,new Message(),future -> {
            Console.print("Request "+ requestInfo.name+" sended with success!", Level.FINE);
        },args);
    }

    public RequestPacket sendRequest(RequestInfo requestInfo, Message message, Object... args){
        return this.sendRequest(requestInfo,message,null,args);
    }

    public RequestPacket sendRequest(RequestInfo requestInfo, boolean notifiedWhenSent, Object... args){
        if(notifiedWhenSent){
          return this.sendRequest(requestInfo,new Message(),future -> {
                System.out.println("Request"+ requestInfo.name+" sended with success!");
            },args);

        }
       return this.sendRequest(requestInfo,new Message(),null,args);
    }

    public RequestPacket sendRequest(RequestInfo requestInfo, Message message, boolean notifiedWhenSent, Object... args){
        if(notifiedWhenSent){
            return this.sendRequest(requestInfo,message,future -> {
                System.out.println("Request"+ requestInfo.name+" sended with success!");
            },args);

        }
        return this.sendRequest(requestInfo,message,null,args);
    }
    public RequestPacket getRequest(int RID){
        return requests.get(RID);
    }
}
