package be.alexandre01.dreamnetwork.api.connection.core.request;


import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreReceiver;
import be.alexandre01.dreamnetwork.api.connection.core.handler.ICoreHandler;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;

import java.util.HashMap;
import java.util.logging.Level;

@Getter
public abstract class AbstractRequestManager implements IRequestManager {
    RequestBuilder requestBuilder;

    @Getter private static HashMap<String, CoreReceiver.RequestInterceptor> tasks = new HashMap<>();


    public AbstractRequestManager(){
        requestBuilder = new RequestBuilder();
      //  requestBuilder.addRequestBuilder();
    }

    public AbstractRequestManager(ChannelHandlerContext ctx, ICoreHandler handler) {
        requestBuilder = new RequestBuilder();
    }

    public RequestPacket sendRequest(RequestPacket request,Object... args){
        return sendRequest(request.getRequestInfo(),new Message(),null,args);
    }
    public abstract RequestPacket sendRequest(RequestInfo requestInfo, Message message, GenericFutureListener<? extends Future<? super Void>> listener, Object... args);

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

    public abstract RequestPacket getRequest(RequestInfo requestInfo, Message message, GenericFutureListener<? extends Future<? super Void>> listener, Object... args);

    public RequestPacket getRequest(RequestInfo requestInfo, Message message, Object... args){
        return getRequest(requestInfo,message,null,args);
    }

    public RequestPacket getRequest(RequestInfo requestInfo, boolean notifiedWhenSent, Object... args){
        return getRequest(requestInfo,new Message(),notifiedWhenSent,args);
    }

    public RequestPacket getRequest(RequestInfo requestInfo, Message message, boolean notifiedWhenSent, Object... args){
        return getRequest(requestInfo,message,null,args);
    }

    public RequestPacket getRequest(RequestInfo requestInfo, Object... args){
        return getRequest(requestInfo,new Message(),null,args);
    }
}
