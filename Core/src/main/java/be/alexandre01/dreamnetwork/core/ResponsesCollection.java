package be.alexandre01.dreamnetwork.core;

import be.alexandre01.dreamnetwork.api.connection.core.communication.IResponsesCollection;
import be.alexandre01.dreamnetwork.api.connection.core.communication.packets.handler.PacketRequestConverter;
import be.alexandre01.dreamnetwork.core.connection.core.communication.services.BaseReceiver;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 07/10/2023 at 14:09
*/
public class ResponsesCollection extends IResponsesCollection {
    public ResponsesCollection(){
        System.out.println("BASE RESPONSE");
        addResponse("BaseResponse",new BaseReceiver());
        addResponse("PacketRequestConverter",new PacketRequestConverter());
    }
}
