package be.alexandre01.dreamnetwork.api.connection.core.request;


import be.alexandre01.dreamnetwork.api.utils.messages.Message;
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
     * @param requestInfo
     * @param message
     * @param args
     * @return
     */
    public RequestPacket sendRequest(RequestInfo requestInfo, Message message, Object... args);

    /**
     * Send a request to the core and catch when the request is sent
     * @param requestInfo
     * @param args
     * @return
     */
    public RequestPacket sendRequest(RequestInfo requestInfo, Object... args);

    /**
     * Send a request to the core and catch when the request is sent
     * @param requestInfo
     * @param notifiedWhenSent
     * @param args
     * @return
     */
    public RequestPacket sendRequest(RequestInfo requestInfo, boolean notifiedWhenSent, Object... args);


    /**
     * Send a request to the core and catch when the request is sent
     * @param requestInfo
     * @param message
     * @param listener
     * @param args
     * @return
     */

    RequestPacket sendRequest(RequestInfo requestInfo, Message message, GenericFutureListener<? extends Future<? super Void>> listener, Object... args);


    /**
     * send a request to the core and catch when the request is sent
     * @param requestInfo
     * @param message
     * @param notifiedWhenSent
     * @param args
     * @return
     */
    public RequestPacket sendRequest(RequestInfo requestInfo, Message message, boolean notifiedWhenSent, Object... args);


    /**
     * get the request from response id
     * @param RID
     * @return
     */

    RequestBuilder getRequestBuilder();

    RequestPacket getRequest(RequestInfo requestInfo, Message message, GenericFutureListener<? extends Future<? super Void>> listener, Object... args);

    RequestPacket getRequest(RequestInfo requestInfo, Message message, Object... args);

    RequestPacket getRequest(RequestInfo requestInfo, boolean notifiedWhenSent, Object... args);

    RequestPacket getRequest(RequestInfo requestInfo, Message message, boolean notifiedWhenSent, Object... args);

    RequestPacket getRequest(RequestInfo requestInfo, Object... args);

}
