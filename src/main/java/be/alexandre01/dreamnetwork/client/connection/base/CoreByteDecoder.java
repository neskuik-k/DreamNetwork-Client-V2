package be.alexandre01.dreamnetwork.client.connection.base;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class CoreByteDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < Character.BYTES) {
            return; // (3)
        }
        //final ByteBuf text = ctx.alloc().buffer(4);
        out.add(in.readBytes(in.readableBytes())); // (4)
    }
}
