package com.wootion.agvrobot.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class HTTPInitializer extends ChannelInitializer<SocketChannel> {

    protected void initChannel(SocketChannel socketChannel) throws           Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        //pipeline.addLast("logerHandler", new LoggingHandler(LogLevel.ERROR));
        pipeline.addLast("httpServerCodec", new HttpServerCodec());
        pipeline.addLast("httpHandler", new HttpServerHandler());

    }
}
