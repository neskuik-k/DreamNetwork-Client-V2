package be.alexandre01.dreamnetwork.core.websocket;

import at.favre.lib.crypto.bcrypt.BCrypt;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.utils.messages.WebMessage;
import be.alexandre01.dreamnetwork.core.connection.core.communication.RateLimiter;
import be.alexandre01.dreamnetwork.core.rest.DreamRestAPI;
import be.alexandre01.dreamnetwork.core.websocket.sessions.WebSession;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.Optional;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 20/11/2023 at 10:35
*/
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    WebSocketServerHandshaker handshaker;
    DreamRestAPI restAPI;
    WebSocketServerInitializer initializer;
    Optional<WebSession> session = Optional.empty();
    public HttpServerHandler(DreamRestAPI restAPI,WebSocketServerInitializer initializer) {
        this.restAPI = restAPI;
        this.initializer = initializer;
    }
   // RateLimiter rateLimiter = Core.getInstance().getRateLimiter();
    RateLimiter rateLimiter = new RateLimiter();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if(rateLimiter.isRateLimited(ctx.channel().remoteAddress().toString())){
              ctx.close();
              return;
        };
        if(msg instanceof ByteBuf){
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            System.out.println("ByteBuf : " + new String(bytes));
            ctx.close();
            return;
        }
        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;

            System.out.println("Http Request Received");

            HttpHeaders headers = httpRequest.headers();
            System.out.println("Connection : " +headers.get("Connection"));
            System.out.println("Upgrade : " + headers.get("Upgrade"));
            System.out.println("Content-Length : " + headers.get("Content-Length"));
            System.out.println(headers.toString());
            if(!headers.contains("Sec-WebSocket-Protocol")){
                System.out.println("Close");
                ctx.close();
                return;
            }

            String dreamSecure = headers.get("Sec-WebSocket-Protocol");
            System.out.println("Sec-WebSocket-Protocol : " + dreamSecure);
            String currentSocket = restAPI.getCurrentKey();

            System.out.println("Current Socket : " + currentSocket);
            currentSocket = currentSocket.split(";")[0];
            try {
                BCrypt.Result result = BCrypt.verifyer().verify(dreamSecure.toCharArray(),currentSocket.toCharArray());
                System.out.println("Result : " + result.verified);
                if(!result.verified && !dreamSecure.equals("Test")){
                    System.out.println(dreamSecure);
                    System.out.println("Socket not valid");
                    ctx.close();
                    return;
                }
                System.out.println("Hmm");
                String refreshSocket = restAPI.checkup("eyJzZWNyZXQiOiJpdElLeHNlTGlDcm1scnB1bzZMWWV4R2c5dktCZUk0TDdOaGdoSmcxR0lSTndMamk2MGFnY0VqODR1Z1dBa29LQVVNa2ZVUVI5R1RpeURJZzVpMmhJeVdkMDBZOWFyT09nUWNXT3BFMFNBRlVMakJxMTR6dENybVBoa3hDUDV4N1U2aExQWUd6NkVQd3NVa0xJbUhvTVR2VjVSQXZMSVpyaHdndWdCWGFDdGxqdlN1NXFEcmtsc3AwdWNPb3VrMWc2bXd6N1RoOEx4NW80MWdDb3EydzdhRmtzcXBSSEtwYmNhZlVmQTB4bmdBd3NPQ1ZQREtVdzlacnJ1T0w5MWlmIiwidXVpZCI6ImY5YjRiMDA4LTJhOGQtNDJmNi05MDA5LThjOTgxZTcxMzIwZiJ9", String.valueOf(initializer.getPort()));
                System.out.println(headers.get(HttpHeaderNames.CONNECTION));
                System.out.println(headers.get(HttpHeaderNames.UPGRADE));
                System.out.println(headers.get(HttpHeaderNames.CONNECTION).toLowerCase());
                if(headers.get(HttpHeaderNames.CONNECTION).toLowerCase().contains("upgrade")){
                    if ("WebSocket".equalsIgnoreCase(headers.get(HttpHeaderNames.UPGRADE))) {

                        //Adding new handler to the existing pipeline to handle WebSocket Messages
                        WebSocketHandler webSocketHandler = new WebSocketHandler(this,ctx);
                        session = Optional.of(webSocketHandler.getWebSession());
                        ctx.pipeline().replace(this, "websocketHandler", webSocketHandler);

                        System.out.println("WebSocketHandler added to the pipeline");
                        System.out.println("Opened Channel : " + ctx.channel());
                        System.out.println("Handshaking....");
                        //Do the Handshake to upgrade connection from HTTP to WebSocket protocol
                        handleHandshake(ctx, httpRequest,dreamSecure);
                        System.out.println("Handshake is done");


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    while (true){
                                        Thread.sleep(5);
                                        //System.out.println("Sending Pong from server");
                                        if(!ctx.channel().isOpen()){
                                            break;
                                        }
                                        //  ctx.channel().writeAndFlush(new TextWebSocketFrame(new WebMessage().put("test","test").toString()));
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            }catch (Exception e){
                Console.bug(e);
            }

        } else {
            System.out.println("Incoming request is unknown => "+ msg + " => " + msg.getClass());
        }
    }
    /* Do the handshaking for WebSocket request */
    protected void handleHandshake(ChannelHandlerContext ctx, HttpRequest req, String subprotocols) {

        WebSocketServerHandshakerFactory wsFactory =
                new WebSocketServerHandshakerFactory(getWebSocketURL(req), subprotocols, true);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    protected String getWebSocketURL(HttpRequest req) {
        System.out.println("Req URI : " + req.getUri());
        String url =  initializer.getPrefix() + req.headers().get("Host") + req.getUri() ;
        System.out.println("Constructed URL : " + url);
        return url;
    }
}
