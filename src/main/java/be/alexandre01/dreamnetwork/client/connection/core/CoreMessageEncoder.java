package be.alexandre01.dreamnetwork.client.connection.core;

import be.alexandre01.dreamnetwork.client.console.Console;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

public class CoreMessageEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf output) throws Exception {
             if (o instanceof ByteBuf) {
                ByteBuf bb = (ByteBuf) o;
                output.writeInt(bb.readableBytes());
                output.writeBytes(bb);
            }
    }
}
