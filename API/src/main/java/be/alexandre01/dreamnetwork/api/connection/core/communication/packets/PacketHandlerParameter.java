package be.alexandre01.dreamnetwork.api.connection.core.communication.packets;

import lombok.Builder;

import java.lang.reflect.Parameter;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 22:17
*/
@Builder
public class PacketHandlerParameter {
    Parameter parameter;
    String key;
}
