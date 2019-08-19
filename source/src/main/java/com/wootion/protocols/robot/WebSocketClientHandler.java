package com.wootion.protocols.robot;

import com.wootion.task.ReadScaleQueue;
import com.wootion.utiles.DataCache;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wootion.robot.MemUtil;

import java.net.InetSocketAddress;

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 * 处理从前端页面发送的请求
 */
public class WebSocketClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketClientHandler.class);

    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;

    public WebSocketClientHandler(final WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        logger.info("handlerAdded");
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        System.out.println("WebSocket Client disconnected!");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(ch, (FullHttpResponse) msg);
            handshakeFuture.setSuccess();

            // get connected
            String robotIp = handshaker.uri().getHost();
            String serverIp= DataCache.getSysParamStr("ros.serverIp");
            if(robotIp.equals(serverIp)){
                ReadScaleQueue.addEvent( ch);
            }
            MemUtil.setRobotCh(MemUtil.queryRobot(robotIp),ch);
            logger.warn("Init RosBridgeClient, set ch {}", ch);
            return;
        }

        if (msg instanceof FullHttpResponse) {
            final FullHttpResponse response = (FullHttpResponse) msg;
            throw new Exception("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content="
                    + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        // final WebSocketFrame frame = (WebSocketFrame) msg;
        if (msg instanceof WebSocketFrame) {
//            System.out.println("This is a WebSocket frame");
//            System.out.println("Client Channel : " + ctx.channel());
            if (msg instanceof BinaryWebSocketFrame) {
                System.out.println("BinaryWebSocketFrame Received : ");
                System.out.println(((BinaryWebSocketFrame) msg).content());
            } else if (msg instanceof TextWebSocketFrame) {
//                System.out.println("TextWebSocketFrame Received : ");
//                ctx.channel().write(new TextWebSocketFrame("Message recieved : " + ((TextWebSocketFrame) msg).text()));
                //System.out.println(((TextWebSocketFrame) msg).text());
                ctx.fireChannelRead(msg);
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

    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        cause.printStackTrace();

        if (!handshakeFuture.isDone()) {
            System.out.println("handshake failed " + cause);
            handshakeFuture.setFailure(cause);
        }

        ctx.close();
    }
}
