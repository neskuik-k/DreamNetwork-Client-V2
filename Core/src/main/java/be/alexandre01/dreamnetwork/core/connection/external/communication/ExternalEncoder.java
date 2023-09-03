package be.alexandre01.dreamnetwork.core.connection.external.communication;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ExternalEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf output) throws Exception {
        if (o instanceof ByteBuf) {
            ByteBuf bb = (ByteBuf) o;
            output.writeInt(bb.readableBytes());
            output.writeBytes(bb);
        } else {
            throw new IllegalArgumentException("Unknown packet type: " + o.getClass().getName());
        }
    }
}
