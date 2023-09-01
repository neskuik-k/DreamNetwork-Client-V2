package be.alexandre01.dreamnetwork.core.connection.external.requests.generated;

import be.alexandre01.dreamnetwork.api.connection.request.RequestBuilder;
import be.alexandre01.dreamnetwork.api.connection.request.RequestType;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.core.commands.lists.sub.addon.List;
import be.alexandre01.dreamnetwork.core.console.Console;


import java.util.ArrayList;

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
            message.setCustomObject("executors", args[0]);
            return message;
        });

    }
}
