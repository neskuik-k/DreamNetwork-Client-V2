package be.alexandre01.dreamnetwork.core.connection.core.requests.external;

import be.alexandre01.dreamnetwork.api.connection.core.request.RequestBuilder;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;
import be.alexandre01.dreamnetwork.api.service.ConfigData;
import be.alexandre01.dreamnetwork.core.service.JVMExecutor;

public class DefaultExternalRequest extends RequestBuilder {
    public DefaultExternalRequest(){
        super.getRequestData().put(RequestType.CORE_HANDSHAKE_STATUS,(message, client, args) -> {
            message.set("STATUS",((String)args[0]).toUpperCase());
            message.set("ID",args[1]);
            return message;
        });

        super.getRequestData().put(RequestType.CORE_START_SERVER,(message, client, args) -> {
            message.set("SERVERNAME",args[0]);
            if(args[1] instanceof ConfigData){
                message.set("DATA",args[1], ConfigData.class);
            }

            if(args[1] instanceof String){
                message.set("PROFILE",args[1]);
            }
            return message;
        });
    }
}
