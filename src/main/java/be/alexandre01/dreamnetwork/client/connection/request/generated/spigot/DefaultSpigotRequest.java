package be.alexandre01.dreamnetwork.client.connection.request.generated.spigot;

import be.alexandre01.dreamnetwork.client.connection.request.RequestBuilder;
import be.alexandre01.dreamnetwork.client.connection.request.RequestType;

public class DefaultSpigotRequest extends RequestBuilder {
    public DefaultSpigotRequest() {
        requestData.put(RequestType.SPIGOT_HANDSHAKE_SUCCESS,(message, args) -> {
            message.set("STATUS","SUCCESS");
            return message;
        });
    }
}
