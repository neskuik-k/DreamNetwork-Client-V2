package be.alexandre01.dreamnetwork.core.websocket;

import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.core.connection.core.ByteCounting;
import be.alexandre01.dreamnetwork.core.connection.core.ByteCountingInboundHandler;
import be.alexandre01.dreamnetwork.core.connection.core.ByteCountingOutboundHandler;
import be.alexandre01.dreamnetwork.core.websocket.ssl.read.AutoReadSSL;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

import java.util.Optional;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 20/11/2023 at 10:34
*/
public class HTTPInitializer extends ChannelInitializer<SocketChannel> {
    WebSocketServerInitializer initializer;
    String prefix = "wss://";
    SslContext sslContext = null;
    public HTTPInitializer(WebSocketServerInitializer initializer)   {
        this.initializer = initializer;
        Optional<AutoReadSSL> currentSSL = Optional.empty();
        try {
           currentSSL = AutoReadSSL.getCurrentSSL();
        }catch (Exception e){
            // ignore
        }

        if(currentSSL.isPresent()){
           try {
               sslContext = currentSSL.get().read();
           }catch (RuntimeException e){
               System.out.println("Error while reading ssl context");
               System.out.println("Try to reconfigure your ssl settings with the command hypervisor ssl");
               Console.bug(e);
           }
        }
    }
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
     //   SSLEngine sslEngine = SSLContext.getDefault().createSSLEngine();
        if(sslContext != null){
            pipeline.addLast(sslContext.newHandler(socketChannel.alloc()));
        }
        //add ip whitelist (on which ip the server can be accessed)
        pipeline.addLast("filter", new HttpFilter(initializer.getWhitelistRemote()));
        ByteCounting byteCounting = new ByteCounting();
        ByteCountingInboundHandler byteCountingInboundHandler = new ByteCountingInboundHandler(byteCounting);
        ByteCountingOutboundHandler byteCountingOutboundHandler = new ByteCountingOutboundHandler(byteCounting);
        pipeline.addLast("byteCountingIn", byteCountingInboundHandler);
        pipeline.addLast("byteCountingOut", byteCountingOutboundHandler);
        pipeline.addLast("httpServerCodec", new HttpServerCodec());

       // dreamRestAPI.checkup("eyJzZWNyZXQiOiJpdElLeHNlTGlDcm1scnB1bzZMWWV4R2c5dktCZUk0TDdOaGdoSmcxR0lSTndMamk2MGFnY0VqODR1Z1dBa29LQVVNa2ZVUVI5R1RpeURJZzVpMmhJeVdkMDBZOWFyT09nUWNXT3BFMFNBRlVMakJxMTR6dENybVBoa3hDUDV4N1U2aExQWUd6NkVQd3NVa0xJbUhvTVR2VjVSQXZMSVpyaHdndWdCWGFDdGxqdlN1NXFEcmtsc3AwdWNPb3VrMWc2bXd6N1RoOEx4NW80MWdDb3EydzdhRmtzcXBSSEtwYmNhZlVmQTB4bmdBd3NPQ1ZQREtVdzlacnJ1T0w5MWlmIiwidXVpZCI6ImY5YjRiMDA4LTJhOGQtNDJmNi05MDA5LThjOTgxZTcxMzIwZiJ9", String.valueOf(initializer.getPort()));
        pipeline.addLast("httpHandler", new HttpServerHandler(initializer.getDreamRestAPI(),initializer));
    }


}
