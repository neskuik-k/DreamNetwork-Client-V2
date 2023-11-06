package be.alexandre01.dreamnetwork.api.connection.core.communication.packets.handler;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.connection.core.communication.CoreReceiver;
import be.alexandre01.dreamnetwork.api.connection.core.communication.UniversalConnection;
import be.alexandre01.dreamnetwork.api.connection.core.communication.packets.PacketHandlingFactory;
import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import io.netty.channel.ChannelHandlerContext;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 05/11/2023 at 12:42
*/
public class PacketRequestConverter extends CoreReceiver {
    final PacketHandlingFactory factory = DNCoreAPI.getInstance().getPacketFactory();

    @Override
    protected void onReceive(Message message, ChannelHandlerContext ctx, UniversalConnection client) throws Exception {
        if(message.hasHeader()){
            String header = message.getHeader();
            if(factory.getHeaders().containsKey(header)){
                factory.getHeaders().get(header).execute(message,ctx);
            }
        }
    }

}
