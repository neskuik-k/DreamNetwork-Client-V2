package be.alexandre01.dreamnetwork.core.connection.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CoreMessageEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf output) throws Exception {
        if(o instanceof byte[]){
            byte[] bytes = (byte[]) o;
            output.writeInt(bytes.length);
            output.writeBytes(bytes);
        }else
        if (o instanceof ByteBuf) {
            ByteBuf bb = (ByteBuf) o;
            output.writeInt(bb.readableBytes());
            output.writeBytes(bb);
        }
    }
}
