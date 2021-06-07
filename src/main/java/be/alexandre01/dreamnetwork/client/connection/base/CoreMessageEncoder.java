package be.alexandre01.dreamnetwork.client.connection.base;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class CoreMessageEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf msg) throws Exception {
        ByteBuf m = (ByteBuf) msg; // (1)
        try {
            m.toString(StandardCharsets.UTF_8);
        } finally {
            m.release();
        }
    }
}
