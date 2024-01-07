package be.alexandre01.dreamnetwork.core.connection.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;


public class ByteCountingInboundHandler extends ChannelInboundHandlerAdapter {
    @Getter private final ByteCounting byteCounting;
    public ByteCountingInboundHandler(ByteCounting byteCounting) {
        this.byteCounting = byteCounting;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        int length = in.readableBytes();
        byteCounting.newBytes(length, ByteCounting.Type.INBOUND);
        //System.out.println("New bytes received : " + length);
        // Call the next handler in the pipeline
        ctx.fireChannelRead(msg);
    }

}