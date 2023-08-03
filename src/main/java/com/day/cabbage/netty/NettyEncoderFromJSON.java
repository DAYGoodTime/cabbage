package com.day.cabbage.netty;

import cn.hutool.json.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

public class NettyEncoderFromJSON extends MessageToByteEncoder<JSONObject> {

    @Override
    protected void encode(ChannelHandlerContext ctx, JSONObject msg, ByteBuf out) throws Exception {
        byte[] bytes = msg.toString().getBytes(StandardCharsets.UTF_8);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
