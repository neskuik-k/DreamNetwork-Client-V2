package be.alexandre01.dreamnetwork.client.connection.core.channels;


import be.alexandre01.dreamnetwork.client.connection.request.ReceivedPacket;
import lombok.Getter;

@Getter
public abstract class DNChannel {
    private String name;

    public DNChannel(String name){
        this.name = name;
    }

    public abstract void received(ReceivedPacket receivedPacket);
}
