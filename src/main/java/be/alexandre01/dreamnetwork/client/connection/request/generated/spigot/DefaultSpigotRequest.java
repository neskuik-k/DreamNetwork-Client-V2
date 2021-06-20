package be.alexandre01.dreamnetwork.client.connection.request.generated.spigot;

import be.alexandre01.dreamnetwork.client.connection.request.RequestBuilder;
import be.alexandre01.dreamnetwork.client.connection.request.RequestType;

public class DefaultSpigotRequest extends RequestBuilder {
    public DefaultSpigotRequest() {
        requestData.put(RequestType.SPIGOT_HANDSHAKE_SUCCESS,(message,client, args) -> {
            message.set("STATUS","SUCCESS");
            message.set("PROCESSNAME", client.getJvmService().getJvmExecutor().getName()+"-"+client.getJvmService().getId());
            return message;
        });
    }
}
