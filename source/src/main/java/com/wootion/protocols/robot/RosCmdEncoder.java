package com.wootion.protocols.robot;

import com.alibaba.fastjson.JSONObject;
import com.wootion.protocols.robot.msg.Publish;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

public class RosCmdEncoder extends MessageToMessageEncoder<Publish<?>> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Publish<?> msg, List<Object> out) throws Exception {
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(JSONObject.toJSON(msg).toString());
        //System.out.println(JSONObject.toJSON(msg).toString());
        out.add(textWebSocketFrame);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println("except in Encode:" + cause);
        cause.printStackTrace();
    }
}
