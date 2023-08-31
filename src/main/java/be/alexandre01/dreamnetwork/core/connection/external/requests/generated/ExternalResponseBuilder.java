package be.alexandre01.dreamnetwork.core.connection.external.requests.generated;

import be.alexandre01.dreamnetwork.api.connection.request.RequestBuilder;
import be.alexandre01.dreamnetwork.api.connection.request.RequestType;

public class ExternalResponseBuilder extends RequestBuilder {
    public ExternalResponseBuilder() {
        super.requestData.put(RequestType.CORE_HANDSHAKE,(message, client, args) -> {
            message.set("INFO", "ExternalDream");
            return message;
        });

        super.requestData.put(RequestType.CORE_REGISTER_EXTEXECUTOR, (message, client, args) -> {
            message.set("EXECUTOR", args[0]);
            return message;
        });

    }
}
