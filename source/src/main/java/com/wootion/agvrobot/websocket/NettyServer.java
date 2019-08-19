package com.wootion.agvrobot.websocket;

import com.wootion.utiles.DataCache;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NettyServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private int port = 0; // default 9000
    // Configure the server.
    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;
    ChannelFuture channelFuture;

    public NettyServer() {

    }
    public NettyServer(int port) {
        this.port = port;
    }



    public void initialiseNettyServer() {
        String url= DataCache.getSysParamStr("ros.webSocketSvr");
        url=url.substring(url.indexOf(":")+1);
        port=Integer.parseInt(url);

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.option(ChannelOption.SO_BACKLOG, 1024);
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                //.handler(new LoggingHandler(LogLevel.ERROR))
                .childHandler(new HTTPInitializer());

        channelFuture = b.bind(port);
        logger.info("NettyServer: start websocket server on ws://localhost:"+port);
    }

    public void shutdownNettyServer() {
        Channel ch = null;
        try {
            ch = channelFuture.sync().channel();
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        logger.info("NettyServer: stop websocket server done");
    }


}
