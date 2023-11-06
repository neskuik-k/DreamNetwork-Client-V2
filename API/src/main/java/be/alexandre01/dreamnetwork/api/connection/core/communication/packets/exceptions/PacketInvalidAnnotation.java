package be.alexandre01.dreamnetwork.api.connection.core.communication.packets.exceptions;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 22:35
*/
public class PacketInvalidAnnotation extends Exception{
    public PacketInvalidAnnotation(String message) {
        super(message);
    }

    public PacketInvalidAnnotation(String message, Throwable cause) {
        super(message, cause);
    }
}
