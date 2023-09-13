package be.alexandre01.dreamnetwork.api.connection.core.request;

import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Synchronized;

import java.util.function.Supplier;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 06/09/2023 at 10:18
*/

@AllArgsConstructor @Getter
public class DNCallback {
    private static int currentId;

    Packet packet;
    TaskHandler handler;

    private static DNCallback on(Packet packet, TaskHandler handler){
        Message message = packet.getMessage();
        message.setInRoot("MID",getNextId());
        return new DNCallback(packet,handler);
    }

    public static DNCallback single(Packet packet, TaskHandler handler){
           return on(packet,handler);
    }

    public static DNCallback multiple(Packet packet, Supplier<TaskHandler> handler){
        return null;
    }

    @Synchronized
    private static int getNextId(){
        return currentId++;
    }

    public void send(){
        packet.getReceiver().writeAndFlush(packet.getMessage());
    }

}
