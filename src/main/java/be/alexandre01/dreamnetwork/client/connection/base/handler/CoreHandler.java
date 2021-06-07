package be.alexandre01.dreamnetwork.client.connection.base.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CoreHandler extends ChannelInboundHandlerAdapter {
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    @Override
    public void channelRegistered(final ChannelHandlerContext ctx) {
        System.out.println("Wow une nouvelle connection");
        System.out.println("Local ADRESS" + ctx.channel().localAddress());
        System.out.println("Remote ADRESS" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) { // (1)
        executorService.scheduleAtFixedRate(() -> {
            byte[] entry = "Hello".getBytes(StandardCharsets.UTF_8);
            final ByteBuf time = ctx.alloc().buffer(entry.length); // (2)
            time.writeBytes(entry);

            final ChannelFuture f = ctx.writeAndFlush(time); // (3)
        /*f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                assert f == future;
                ctx.close();
            }
        }); // (4)*/
        },1,10, TimeUnit.SECONDS);

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if(executorService != null){
            System.out.println("WOW");
            executorService.shutdown();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
