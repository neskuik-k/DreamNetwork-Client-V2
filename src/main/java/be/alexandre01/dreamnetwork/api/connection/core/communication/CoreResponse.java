package be.alexandre01.dreamnetwork.api.connection.core.communication;


import be.alexandre01.dreamnetwork.api.connection.request.RequestInfo;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;

import java.util.LinkedHashMap;

public abstract class CoreResponse {
    private final LinkedHashMap<Integer,RequestInterceptor> map = new LinkedHashMap<>();



    public void onAutoResponse(Message message, ChannelHandlerContext ctx, IClient client){
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
            onResponse(message,ctx,client);
        } catch (Exception e) {
            Console.bug(e);
        }
    }

    protected void onResponse(Message message, ChannelHandlerContext ctx, IClient client) throws Exception {
        // override this
    }

    public void addRequestInterceptor(RequestInfo requestInfo, RequestInterceptor requestInterceptor){
        map.put(requestInfo.id(),requestInterceptor);
    }

    private RequestInterceptor getRequestInterceptor(Message message){
        return map.get(message.getRequestID());
    }
    public interface RequestInterceptor {
        public void onRequest(Message message, ChannelHandlerContext ctx, IClient client) throws Exception;
    }

    protected boolean preReader(Message message, ChannelHandlerContext ctx, IClient client){
        // do nothing
        return true;
    }
}
