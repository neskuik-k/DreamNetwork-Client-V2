package be.alexandre01.dreamnetwork.api.connection.core.communication.packets;

import lombok.AllArgsConstructor;
import lombok.Builder;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 21:37
*/

@AllArgsConstructor
@Builder
public class RequestHandler {
    String id;
    int priority;
    String[] channels;
    PacketCastOption castOption;
    String id(){
        return id;
    }
    int priority(){
        return priority;
    }

    String[] channels(){
        return channels;
    }

    PacketCastOption castOption(){
        return castOption;
    }

    public enum PacketCastOption {
        NOT_SET,
        NOT_NULL,
        NULLABLE,
        IGNORE_ALL,
    }
}
