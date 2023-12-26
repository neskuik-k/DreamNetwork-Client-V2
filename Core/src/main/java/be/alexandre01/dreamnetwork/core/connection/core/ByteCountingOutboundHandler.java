package be.alexandre01.dreamnetwork.core.connection.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.Getter;


public class ByteCountingOutboundHandler extends ChannelOutboundHandlerAdapter {
    @Getter private final ByteCounting byteCounting;
    public ByteCountingOutboundHandler(ByteCounting byteCounting) {
        this.byteCounting = byteCounting;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        int length = in.readableBytes();
        byteCounting.newBytes(length, ByteCounting.Type.OUTBOUND);
        // Call the next handler in the pipeline
        ctx.write(msg, promise);
    }
}