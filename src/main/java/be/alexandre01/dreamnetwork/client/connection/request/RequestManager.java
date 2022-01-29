package be.alexandre01.dreamnetwork.client.connection.request;


import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.client.connection.request.exception.RequestNotFoundException;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;

import java.util.HashMap;
import java.util.logging.Level;

public class RequestManager {
    @Getter
    RequestBuilder requestBuilder;
    private ClientManager.Client client;
    private HashMap<Integer, RequestPacket> requests = new HashMap<>();

    public RequestManager(ClientManager.Client client){
        this.client = client;
        requestBuilder = new RequestBuilder();
        requestBuilder.addRequestBuilder();
    }

    public RequestPacket sendRequest(RequestPacket request,Object... args){
        RequestBuilder.RequestData requestData = requestBuilder.requestData.get(request.getRequestType());
        request.setClient(client);
        request.setMessage(requestData.write(request.getMessage(),client,args));
        request.getClient().writeAndFlush(request.getMessage(),request.getListener());
        requests.put(request.getRequestID(),request);
        return request;
    }
    public RequestPacket sendRequest(RequestType requestType, Message message, GenericFutureListener<? extends Future<? super Void>> listener, Object... args){
         if(!requestBuilder.requestData.containsKey(requestType)){
             try {
                 throw new RequestNotFoundException(requestType);
             } catch (RequestNotFoundException e) {
                 e.printStackTrace();
             }
         }

         RequestBuilder.RequestData requestData = requestBuilder.requestData.get(requestType);
         message.setHeader("RequestType");
        message.setRequestType(requestType);
        RequestPacket request = new RequestPacket(requestType,requestData.write(message,client,args),listener);
        request.setClient(client);
        request.getClient().writeAndFlush(request.getMessage(),listener);
        requests.put(request.getRequestID(),request);
        return request;
         //client.writeAndFlush(requestData.write(message,client,args),listener);
    }

    public RequestPacket sendRequest(RequestType requestType, Object... args){
       return this.sendRequest(requestType,new Message(),future -> {
            Console.print("Request "+ requestType.name()+" sended with success!", Level.FINE);
        },args);
    }

    public RequestPacket sendRequest(RequestType requestType, Message message, Object... args){
        return this.sendRequest(requestType,message,null,args);
    }

    public RequestPacket sendRequest(RequestType requestType, boolean notifiedWhenSent, Object... args){
        if(notifiedWhenSent){
          return this.sendRequest(requestType,new Message(),future -> {
                System.out.println("Request"+ requestType.name()+" sended with success!");
            },args);

        }
       return this.sendRequest(requestType,new Message(),null,args);
    }

    public RequestPacket sendRequest(RequestType requestType, Message message, boolean notifiedWhenSent, Object... args){
        if(notifiedWhenSent){
            return this.sendRequest(requestType,message,future -> {
                System.out.println("Request"+ requestType.name()+" sended with success!");
            },args);

        }
        return this.sendRequest(requestType,message,null,args);
    }
    public RequestPacket getRequest(int RID){
        return requests.get(RID);
    }
}
