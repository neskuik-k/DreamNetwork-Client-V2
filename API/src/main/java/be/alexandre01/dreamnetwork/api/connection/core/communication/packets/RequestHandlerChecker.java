package be.alexandre01.dreamnetwork.api.connection.core.communication.packets;

import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.Builder;

import java.lang.reflect.Method;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 04/11/2023 at 22:11
*/

public class RequestHandlerChecker extends HandlerChecker{
    private final RequestHandler requestHandler;


    @Builder
    public RequestHandlerChecker(RequestHandler handler, PacketGlobal packetGlobal, PacketHandlerParameter[] parameters, Method method) {
        super(packetGlobal, parameters, method);
        this.requestHandler = handler;
    }



    public void execute(Message message, ChannelHandlerContext ctx) {
        super.execute(message, ctx, requestHandler.id(), requestHandler.channels(), requestHandler.castOption().name());
    }
}
