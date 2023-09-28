package be.alexandre01.dreamnetwork.core.connection.external.requests.generated;

import be.alexandre01.dreamnetwork.api.connection.core.request.RequestBuilder;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.ConfigData;


import java.util.List;

public class ExternalResponseBuilder extends RequestBuilder {
    public ExternalResponseBuilder() {
        super.requestData.put(RequestType.CORE_HANDSHAKE,(message, client, args) -> {
            message.set("INFO", "ExternalDream");
            return message;
        });

        requestData.put(RequestType.CORE_REGISTER_EXTERNAL_EXECUTORS,(message, client, args) -> {
            if(args.length == 0){
                try {
                    throw new Exception("Error with registering external executors");
                } catch (Exception e) {
                    Console.bug(e);
                }
            }
            message.set("executors", (List<?>) args[0],ConfigData.class);
            return message;
        });

    }
}
