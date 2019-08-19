package com.wootion.protocols.robot;

import com.alibaba.fastjson.JSONObject;
import com.wootion.commons.Constans;
import com.wootion.protocols.robot.msg.*;
import com.wootion.protocols.robot.operation.ServerStatusOp;
import com.wootion.robot.MemUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public class RosBridgeClient {
    private static final Logger logger = LoggerFactory.getLogger(RosBridgeClient.class);
    private final URI uri;
    private boolean ready = false;
    private Channel ch;
    private ChannelFuture channelFuture;
    private WebSocketClientHandler handler;
    private static final EventLoopGroup group = new NioEventLoopGroup();

    public RosBridgeClient(final String uri) {
        this.uri = URI.create(uri);
    }

    public void open() throws Exception {
        Bootstrap b = new Bootstrap();
        String protocol = uri.getScheme();
        if (!"ws".equals(protocol)) {
            throw new IllegalArgumentException("Unsupported protocol: " + protocol);
        }

        // Connect with V13 (RFC 6455 aka HyBi-17). You can change it to V08 or V00.
        // If you change it to V00, ping is not supported and remember to change
        // HttpResponseDecoder to WebSocketHttpResponseDecoder in the pipeline.
        handler =
                new WebSocketClientHandler(
                        WebSocketClientHandshakerFactory.newHandshaker(
                                uri, WebSocketVersion.V13, null, false, EmptyHttpHeaders.INSTANCE, 1280000));

        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //pipeline.addLast(new LoggingHandler(LogLevel.ERROR));
                        pipeline.addLast("http-codec", new HttpClientCodec());
                        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                        pipeline.addLast("ws-handler", handler);
                        pipeline.addLast("rosclient", new RosStatusDecoder());
                        pipeline.addLast("rosencoder", new RosCmdEncoder());
                        pipeline.addLast("rosclientHandler", new RosClientHandler());
                    }
                });
        //System.out.println("WebSocket Client connecting");
        channelFuture = b.connect(uri.getHost(), uri.getPort());
        ch = channelFuture.sync().channel();
        ChannelFuture f = handler.handshakeFuture().sync();
        f.addListener((future) -> {
            if (future.isSuccess()) {
                 ready = true;
                System.out.println("connect success, handshake");
            }
        });
    }

    public void close() throws InterruptedException {
        //System.out.println("WebSocket Client sending close");
        if(ch!=null){
            ch.writeAndFlush(new CloseWebSocketFrame());
            ch.closeFuture().sync();
        }
        group.shutdownGracefully();
    }

    public void send(Publish<?> op) throws IOException {
        if (!ready) {
            logger.warn("send cmd faild, not ready!");
            return;
        }
        ChannelFuture channelFuture = ch.writeAndFlush(op);
        channelFuture.addListener((future) -> {
            if (future.isSuccess()) {
                System.out.println("write success");
            }else {
                System.out.println("failed!" + future.cause());

            }
        }
        );
    }

    public static void main(String[] args) throws Exception {
        final String url = "ws://10.204.157.230:9090/";
        final RosBridgeClient client = new RosBridgeClient(url);
        client.open();
      /*  Advertise advertise = new Advertise(Constans.TOPIC_ROBOT_COMMAND, Constans.TOPIC_ROBOT_COMMAND_TYPE);
        advertise.setOp("advertise");
        //Subscribe subscribe1 = new Subscribe(Constans.FIBONACCI_FEEDBACK_NAME, Constans.FIBONACCI_FEEDBACK_TYPE);
        client.ch.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSON(advertise).toString()));
        //client.ch.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSON(subscribe1).toString()));
        Advertise unAdvertise = new Advertise(Constans.TOPIC_ROBOT_COMMAND, Constans.TOPIC_ROBOT_COMMAND_TYPE);
        unAdvertise.setOp("unAdvertise");
        int opId = MemUtil.newOpId();
        ServerStatusCommandMsg msg = new ServerStatusCommandMsg();

        msg.setTrans_id(opId);
        Header header=new Header();
        header.setSeq(opId);
        msg.setHeader(header);
        ServerStatusOp serverStatusOp = new ServerStatusOp(opId, msg);

        RosCommandExcecutor rosCommandExcecutor=new RosCommandExcecutor(client.ch);
        rosCommandExcecutor.publish(serverStatusOp);
        Thread.sleep(1000);
*/

        /*{
            "op":"publish",
            "id":"publish:/fibonacci/goal:6",
            "topic":"/fibonacci/goal",
            "msg":
                {
                    "goal_id":
                    {
                        "stamp":{"secs":0,"nsecs":0},
                        "id":"goal_0.09562925680091583_1560219432442"
                    },
                    "goal":{"order":7}
                },
            "latch":false
        }*/
/*

        int opId = MemUtil.newOpId();
        FibonacciCommandMsg cmdMsg=new FibonacciCommandMsg();
        cmdMsg.setOrder(5);
        cmdMsg.setTrans_id(opId);

        ActionGoalMsg actionGoalMsg=new ActionGoalMsg();
        actionGoalMsg.setGoal(cmdMsg);
        actionGoalMsg.setId(opId);

        FibonacciGoalOp op=new FibonacciGoalOp();
        op.setId(String.valueOf(opId));
        op.setTopic("/fibonacci/goal");
        op.setId("publish:/fibonacci/goal:10");
        op.setMsg(actionGoalMsg);
        System.out.println(JSONObject.toJSON(op).toString());

        RosActionExcecutor ce=new RosActionExcecutor();
        ce.setCh(client.ch);
        Object result=ce.publish(op);
        System.out.println(result.toString());



        for (int i=0;i<10;i++){
            try {
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        client.ch.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSON(unAdvertise).toString()));

        client.close();
*/
    }
}
