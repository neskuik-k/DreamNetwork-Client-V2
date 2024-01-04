package be.alexandre01.dreamnetwork.core.websocket;

import be.alexandre01.dreamnetwork.api.utils.messages.Message;
import be.alexandre01.dreamnetwork.api.utils.messages.WebMessage;
import be.alexandre01.dreamnetwork.core.websocket.sessions.WebSession;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.*;
import lombok.Getter;

@Getter
public class WebSocketHandler extends ChannelInboundHandlerAdapter  {
    final WebSession webSession;

    public WebSocketHandler(HttpServerHandler serverHandler, ChannelHandlerContext ctx) {
        this.webSession = new WebSession(ctx,serverHandler,this);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //System.out.println("WebSocketHandler channelRead");
        //System.out.println("WebSocketHandler channelRead : " + msg);
        if (msg instanceof WebSocketFrame) {
          //  System.out.println("This is a WebSocket frame");
           // System.out.println("Client Channel : " + ctx.channel());
            if (msg instanceof BinaryWebSocketFrame) {
                System.out.println("BinaryWebSocketFrame Received : ");
                System.out.println(((BinaryWebSocketFrame) msg).content());
            } else if (msg instanceof TextWebSocketFrame) {
               // System.out.println("TextWebSocketFrame Received : ");
                /*ctx.channel().writeAndFlush(
                        new TextWebSocketFrame("Message recieved : " + ((TextWebSocketFrame) msg).text()));*/

               // System.out.println("New message detect !");
                WebMessage webMessage = WebMessage.fromString(((TextWebSocketFrame) msg).text());
                if(webMessage != null){
                    //  System.out.println("Message is not null");
                    //System.out.println("Message : " + webMessage);
                    webSession.getMessageListeners().forEach(messageListener -> messageListener.onRead(webMessage));
                    //webSession.send(webMessage);
                    return;
                }
                System.out.println(((TextWebSocketFrame) msg).text());
            } else if (msg instanceof PingWebSocketFrame) {
                System.out.println("PingWebSocketFrame Received : ");
                System.out.println(((PingWebSocketFrame) msg).content());
            } else if (msg instanceof PongWebSocketFrame) {
                System.out.println("PongWebSocketFrame Received : ");
                System.out.println(((PongWebSocketFrame) msg).content());
            } else if (msg instanceof CloseWebSocketFrame) {
                System.out.println("CloseWebSocketFrame Received : ");
                System.out.println("ReasonText :" + ((CloseWebSocketFrame) msg).reasonText());
                System.out.println("StatusCode : " + ((CloseWebSocketFrame) msg).statusCode());
            } else {
                System.out.println("Unsupported WebSocketFrame");
            }
        }
        System.out.println("Check if msg is ByteBufHolder");
        if(msg instanceof ByteBufHolder){
            // release the buffer
            System.out.println("Release ByteBuf");
            ((ByteBufHolder) msg).release();
            return;
        }
        if(msg instanceof ByteBuf){
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            System.out.println("Unknown ByteBuf detected : " + new String(bytes));

            System.out.println("Close channel");
            ctx.close();
            byteBuf.release();
            return;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        System.out.println("WebSocketHandler channelInactive");
        webSession.close();
    }
}