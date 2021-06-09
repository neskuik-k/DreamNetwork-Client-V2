package be.alexandre01.dreamnetwork.client.connection.request;


import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.connection.core.communication.ClientManager;
import be.alexandre01.dreamnetwork.client.connection.request.exception.RequestNotFoundException;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class RequestManager {
    RequestBuilder requestBuilder;
    private ClientManager.Client client;

    public RequestManager(ClientManager.Client client){
        this.client = client;
        requestBuilder = new RequestBuilder();
        requestBuilder.addRequestBuilder();
    }

    public void sendRequest(RequestType requestType, Message message, GenericFutureListener<? extends Future<? super Void>> listener, String... args){
         if(!requestBuilder.requestData.containsKey(requestType)){
             try {
                 throw new RequestNotFoundException();
             } catch (RequestNotFoundException e) {
                 e.printStackTrace();
             }
         }

         RequestBuilder.RequestData requestData = requestBuilder.requestData.get(requestType);
         message.setHeader("RequestType");

         client.writeAndFlush(requestData.write(message,args),listener);
    }

    public void sendRequest(RequestType requestType, String... args){
        this.sendRequest(requestType,new Message(),null,args);
    }

    public void sendRequest(RequestType requestType,Message message, String... args){
        this.sendRequest(requestType,message,null,args);
    }

    public void sendRequest(RequestType requestType,boolean notifiedWhenSent, String... args){
        if(notifiedWhenSent){
            this.sendRequest(requestType,new Message(),future -> {
                System.out.println("Request"+ requestType.name()+" sended with success!");
            },args);
            return;
        }
        this.sendRequest(requestType,new Message(),null,args);
    }

    public void sendRequest(RequestType requestType,Message message,boolean notifiedWhenSent, String... args){
        if(notifiedWhenSent){
            this.sendRequest(requestType,message,future -> {
                System.out.println("Request"+ requestType.name()+" sended with success!");
            },args);
            return;
        }
        this.sendRequest(requestType,message,null,args);
    }
}
