package be.alexandre01.dreamnetwork.api.connection.request;

import be.alexandre01.dreamnetwork.client.connection.request.RequestPacket;
import be.alexandre01.dreamnetwork.client.utils.messages.Message;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public interface IRequestManager {
    /**
     * Send a request to the core
     * @param request
     * @param args
     * @return
     */
    RequestPacket sendRequest(RequestPacket request, Object... args);

    /**
     * Send a request to the core and catch when the request is sent
     * @param requestType
     * @param message
     * @param args
     * @return
     */
    public RequestPacket sendRequest(RequestType requestType, Message message, Object... args);

    /**
     * Send a request to the core and catch when the request is sent
     * @param requestType
     * @param args
     * @return
     */
    public RequestPacket sendRequest(RequestType requestType, Object... args);

    /**
     * Send a request to the core and catch when the request is sent
     * @param requestType
     * @param notifiedWhenSent
     * @param args
     * @return
     */
    public RequestPacket sendRequest(RequestType requestType, boolean notifiedWhenSent, Object... args);


    /**
     * Send a request to the core and catch when the request is sent
     * @param requestType
     * @param message
     * @param listener
     * @param args
     * @return
     */

    RequestPacket sendRequest(RequestType requestType, Message message, GenericFutureListener<? extends Future<? super Void>> listener, Object... args);


    /**
     * send a request to the core and catch when the request is sent
     * @param requestType
     * @param message
     * @param notifiedWhenSent
     * @param args
     * @return
     */
    public RequestPacket sendRequest(RequestType requestType, Message message, boolean notifiedWhenSent, Object... args);


    /**
     * get the request from response id
     * @param RID
     * @return
     */
    RequestPacket getRequest(int RID);


}
