package be.alexandre01.dreamnetwork.core.connection.external.requests;

import java.util.Collection;

import be.alexandre01.dreamnetwork.api.connection.core.request.AbstractRequestManager;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestBuilder;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestInfo;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestPacket;
import be.alexandre01.dreamnetwork.api.connection.core.request.exceptions.RequestNotFoundException;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import be.alexandre01.dreamnetwork.core.connection.external.handler.ExternalClientHandler;
import be.alexandre01.dreamnetwork.core.connection.external.requests.generated.ExternalResponseBuilder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;

public class ExtRequestManager extends AbstractRequestManager {
    @Getter
    RequestBuilder requestBuilder;
    ExternalClientHandler handler;
    public ExtRequestManager(ExternalClientHandler handler) {
        requestBuilder = new RequestBuilder();
        requestBuilder.addRequestBuilder(new ExternalResponseBuilder());
        this.handler = handler;
    }
    @Override
    public RequestPacket sendRequest(RequestInfo requestInfo, Message message, GenericFutureListener<? extends Future<? super Void>> listener, Object... args) {
        RequestPacket request = getRequest(requestInfo,message,listener,args);
        handler.writeAndFlush(request.getMessage(), listener);
        //requests.put(request.getRequestID(), request);
        return request;
    }

    @Override
    public RequestPacket getRequest(RequestInfo requestInfo, Message message, GenericFutureListener<? extends Future<? super Void>> listener, Object... args) {
        if (!requestBuilder.getRequestData().containsKey(requestInfo)) {
            try {
                throw new RequestNotFoundException(requestInfo,"ExternalClient");
            } catch (RequestNotFoundException e) {
                e.printStackTrace();
            }
        }

        Collection<RequestBuilder.RequestData> requestData = requestBuilder.getRequestData().get(requestInfo);


        message.setHeader("RI");
        message.setRequestInfo(requestInfo);
        if (requestData != null) {
            for (RequestBuilder.RequestData data : requestData) {
                message = data.write(message, null, args);
            }
        }
        if (RequestBuilder.getGlobalRequestData().containsKey(requestInfo)) {
            for (RequestBuilder.RequestData data : RequestBuilder.getGlobalRequestData().get(requestInfo)) {
                message = data.write(message, null, args);
            }
        }
        RequestPacket request = new RequestPacket(requestInfo, message, listener);
        return request;
    }
}
