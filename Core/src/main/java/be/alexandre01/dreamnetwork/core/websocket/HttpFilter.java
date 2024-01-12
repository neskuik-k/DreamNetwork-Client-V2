package be.alexandre01.dreamnetwork.core.websocket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ipfilter.IpFilterRule;
import io.netty.handler.ipfilter.IpFilterRuleType;

import java.net.InetSocketAddress;
import java.util.Set;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 07/01/2024 at 18:34
*/
public class HttpFilter extends ChannelInboundHandlerAdapter {
    private final Set<String> whitelist;

    public HttpFilter(Set<String> whitelist) {
        this.whitelist = whitelist;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String remoteAddress = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        if (whitelist.contains(remoteAddress)) {
            // IP is whitelisted, proceed with the connection*
            System.out.println("IP whitelisted : " + remoteAddress);
            super.channelActive(ctx);
        } else {
            // IP is not whitelisted, close the connection
            System.out.println("IP not whitelisted : " + remoteAddress);
            System.out.println(((InetSocketAddress) ctx.channel().remoteAddress()).getHostString());
            System.out.println(((InetSocketAddress) ctx.channel().remoteAddress()).getHostName());
            super.channelActive(ctx);
            //ctx.close();
        }
    }
}
