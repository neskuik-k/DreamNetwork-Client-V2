package be.alexandre01.dreamnetwork.core.connection.core.requests.external;

import be.alexandre01.dreamnetwork.api.connection.core.request.RequestBuilder;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;

public class DefaultExternalRequest extends RequestBuilder {
    public DefaultExternalRequest(){
        super.getRequestData().put(RequestType.CORE_HANDSHAKE_STATUS,(message, client, args) -> {
            message.set("STATUS",((String)args[0]).toUpperCase());
            return message;
        });
    }
}
