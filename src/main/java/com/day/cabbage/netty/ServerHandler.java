package com.day.cabbage.netty;

import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.day.cabbage.pojo.NettyEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.context.ApplicationEventPublisher;

import java.util.LinkedList;
import java.util.Queue;

public class ServerHandler extends ChannelInboundHandlerAdapter { // (1)

    private ApplicationEventPublisher eventPublisher;

    public ServerHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }


    public static Queue<SocketTask> queue = new LinkedList<>();

    public static boolean isProcessing = false;

    private static final Log logger = LogFactory.get(ServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        JSONObject data = (JSONObject) msg;
        SocketTask Task = new SocketTask(ctx, data);
        queue.add(Task);
        RunTask();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        logger.error("socket消息异常:" + cause.getLocalizedMessage());
        cause.printStackTrace();
        ctx.close();
    }

    private void RunTask() {
        if (isProcessing) return;
        while (!queue.isEmpty()) {
            //TODO doing Tasking
            if (NettyEventHandler.eventProcessing) {
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            {
                SocketTask currentTask = queue.poll();
                logger.info("处理数据~");
                eventPublisher.publishEvent(new NettyEvent(currentTask));
            }
        }
        isProcessing = false;
    }
}