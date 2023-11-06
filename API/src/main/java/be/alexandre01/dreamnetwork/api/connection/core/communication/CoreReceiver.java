package be.alexandre01.dreamnetwork.api.connection.core.communication;


import be.alexandre01.dreamnetwork.api.connection.core.request.RequestInfo;

import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class CoreReceiver {
    private final LinkedHashMap<Integer,RequestInterceptor> map = new LinkedHashMap<>();

    @Getter private final List<CoreReceiver> subReceivers = new ArrayList<>();



    public void onAutoReceive(Message message, ChannelHandlerContext ctx, UniversalConnection client){
        if(!preReader(message,ctx,client)){
            return;
        }
        try {
            if(message.hasRequest()){
                final RequestInterceptor interceptor = map.get(message.getRequestID());
                if (interceptor != null) {
                    interceptor.onRequest(message, ctx, client);
                }
            }
            onReceive(message,ctx,client);
        } catch (Exception e) {
            Console.bug(e);
        }
    }

    protected void onReceive(Message message, ChannelHandlerContext ctx, UniversalConnection client) throws Exception {
        // override this
    }

    public void addRequestInterceptor(RequestInfo requestInfo, RequestInterceptor requestInterceptor){
        map.put(requestInfo.id(),requestInterceptor);
    }

    private RequestInterceptor getRequestInterceptor(Message message){
        return map.get(message.getRequestID());
    }
    public interface RequestInterceptor {
        public void onRequest(Message message, ChannelHandlerContext ctx, UniversalConnection client) throws Exception;
    }

    protected boolean preReader(Message message, ChannelHandlerContext ctx, UniversalConnection client){
        // do nothing
        return true;
    }
}
