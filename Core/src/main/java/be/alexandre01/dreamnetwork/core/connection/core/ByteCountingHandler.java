package be.alexandre01.dreamnetwork.core.connection.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;

@Getter
public class ByteCountingHandler extends ChannelInboundHandlerAdapter {
    private long bytesRead = 0;

   // private static final ResourceLeakDetector<ByteCountingHandler> leakDetector = ResourceLeakDetector.newInstance(ByteCountingHandler.class);
    @Getter private static long totalBytesRead = 0;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        int length = in.readableBytes();
        bytesRead += length;
        totalBytesRead += length;

        // Call the next handler in the pipeline
        ctx.fireChannelRead(msg);
    }

}