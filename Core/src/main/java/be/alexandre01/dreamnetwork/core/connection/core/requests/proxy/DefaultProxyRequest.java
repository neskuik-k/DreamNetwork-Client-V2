package be.alexandre01.dreamnetwork.core.connection.core.requests.proxy;

import be.alexandre01.dreamnetwork.api.connection.core.communication.AServiceClient;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestBuilder;
import be.alexandre01.dreamnetwork.api.connection.core.request.RequestType;
import be.alexandre01.dreamnetwork.api.console.Console;

import java.util.logging.Level;

public class DefaultProxyRequest extends RequestBuilder {
    public DefaultProxyRequest() {
        requestData.put(RequestType.PROXY_HANDSHAKE_SUCCESS,(message, client, args) -> {
            message.set("STATUS","SUCCESS");
            message.set("PROCESSNAME", ((AServiceClient)client).getService().getFullName());
            return message;
        });
        requestData.put(RequestType.PROXY_REGISTER_SERVER,(message,client, args) -> {
            Console.print("REQUEST REGISTER SERVER", Level.FINE);
            message.set("PROCESSNAME",args[0]);
            if(args[1] != null){
                message.set("CUSTOMNAME",args[1]);
            }
            message.set("REMOTEIP",args[2]);
            message.set("PORT",args[3]);
            message.set("MODS",args[4]);
            return message;
        });
        requestData.put(RequestType.PROXY_UNREGISTER_SERVER,(message,client, args) -> {
            Console.fine("REQUEST UNREGISTER SERVER");
            message.set("PROCESSNAME",args[0]);
            return message;
        });
        requestData.put(RequestType.PROXY_EXECUTE_COMMAND,(message,client, args) -> {
            message.set("CMD", args[0]);
            return message;
        });
        requestData.put(RequestType.CORE_STOP_SERVER, ((message, client, args) -> {
          return message;
        }));

        requestData.put(RequestType.CORE_REGISTER_CHANNEL,(message, client, args) -> {
            message.set("CHANNEL", args[0]);
            message.set("MAP",args[1]);
            return message;
        });

        requestData.put(RequestType.CORE_REGISTER_CHANNELS_INFOS,(message, client, args) -> {
            message.set("CHANNELS", args[0]);
            return message;
        });

    }
}
