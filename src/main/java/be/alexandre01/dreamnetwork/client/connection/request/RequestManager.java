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

    public RequestPacket sendRequest(RequestPacket request,String... args){
        RequestBuilder.RequestData requestData = requestBuilder.requestData.get(request.getRequestType());
        request.setClient(client);
        request.setMessage(requestData.write(request.getMessage(),client,args));
        request.getClient().writeAndFlush(request.getMessage(),request.getListener());
        requests.put(request.getRequestID(),request);
        return request;
    }
    public RequestPacket sendRequest(RequestType requestType, Message message, GenericFutureListener<? extends Future<? super Void>> listener, String... args){
         if(!requestBuilder.requestData.containsKey(requestType)){
             try {
                 throw new RequestNotFoundException();
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

    public RequestPacket sendRequest(RequestType requestType, String... args){
       return this.sendRequest(requestType,new Message(),future -> {
            Console.print("Request "+ requestType.name()+" sended with success!", Level.FINE);
        },args);
    }

    public RequestPacket sendRequest(RequestType requestType, Message message, String... args){
        return this.sendRequest(requestType,message,null,args);
    }

    public RequestPacket sendRequest(RequestType requestType, boolean notifiedWhenSent, String... args){
        if(notifiedWhenSent){
          return this.sendRequest(requestType,new Message(),future -> {
                System.out.println("Request"+ requestType.name()+" sended with success!");
            },args);

        }
       return this.sendRequest(requestType,new Message(),null,args);
    }

    public RequestPacket sendRequest(RequestType requestType, Message message, boolean notifiedWhenSent, String... args){
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
