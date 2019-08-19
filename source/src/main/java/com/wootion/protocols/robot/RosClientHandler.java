package com.wootion.protocols.robot;

import com.wootion.task.EventQueue;
import com.wootion.robot.MemUtil;
import com.wootion.commons.Constans;
import com.wootion.robot.MemRobot;
import com.wootion.protocols.robot.msg.*;
import com.wootion.task.event.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class RosClientHandler extends SimpleChannelInboundHandler<Map<?,?>> {//Publish<?>

    private static final Logger logger = LoggerFactory.getLogger(RosClientHandler.class);


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // super.exceptionCaught(ctx, cause);
        System.out.println("except: " + cause.getMessage());
        cause.printStackTrace();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("channel inactive");
        super.channelInactive(ctx);
    }

	@Override
	//protected void channelRead0(ChannelHandlerContext ctx, Publish<?> msg) throws Exception {
    protected void channelRead0(ChannelHandlerContext ctx, Map msg) throws Exception {
	    RosClientMsgHandler.handleMessage(msg,ctx);
    }


}
