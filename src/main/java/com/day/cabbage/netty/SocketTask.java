package com.day.cabbage.netty;

import cn.hutool.json.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SocketTask {
    ChannelHandlerContext ctx;

    JSONObject data;
}
