package com.day.cabbage.netty;

import cn.hutool.json.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class NettyDecoderToJSON extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 如果可读字节数小于 4，说明数据不足，返回等待下一次读取
        if (in.readableBytes() < 4) {
            return;
        }
        // 读取数据长度
        in.markReaderIndex();
        int length = in.readInt();

        // 如果可读字节数小于数据长度，说明数据不足，返回等待下一次读取
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        // 读取数据并转换为 JSONObject 对象
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        String jsonStr = new String(bytes, StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(jsonStr);
        out.add(jsonObject);
    }
}
