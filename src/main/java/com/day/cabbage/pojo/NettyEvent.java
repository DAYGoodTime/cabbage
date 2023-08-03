package com.day.cabbage.pojo;

import com.day.cabbage.netty.SocketTask;

import org.springframework.context.ApplicationEvent;




public class NettyEvent extends ApplicationEvent {
    private SocketTask task;

    public NettyEvent(SocketTask task) {
        super(task);
        this.task = task;
    }

    public SocketTask getTask() {
        return task;
    }
}
