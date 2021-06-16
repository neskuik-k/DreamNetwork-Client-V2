package be.alexandre01.dreamnetwork.client.connection.request.generated.proxy;

import be.alexandre01.dreamnetwork.client.connection.request.RequestBuilder;
import be.alexandre01.dreamnetwork.client.connection.request.RequestType;

public class DefaultBungeeRequest extends RequestBuilder {
    public DefaultBungeeRequest() {
        requestData.put(RequestType.BUNGEECORD_HANDSHAKE_SUCCESS,(message, args) -> {
            message.set("STATUS","SUCCESS");
            return message;
        });
        requestData.put(RequestType.BUNGEECORD_REGISTER_SERVER,(message, args) -> {
            System.out.println("REQUEST REGISTER SERVER");
            System.out.println(args[0]);
            System.out.println(args[1]);
            System.out.println(args[2]);
            message.set("PROCESSNAME",args[0]);
            message.set("REMOTEIP",args[1]);
            message.set("PORT",args[2]);
            return message;
        });
    }
}
