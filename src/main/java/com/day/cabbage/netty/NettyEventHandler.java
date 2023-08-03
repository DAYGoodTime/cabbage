package com.day.cabbage.netty;

import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.day.cabbage.enums.RequestType;
import com.day.cabbage.pojo.Argument;
import com.day.cabbage.pojo.NettyEvent;
import com.day.cabbage.services.BotService;
import com.day.cabbage.services.UserService;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class NettyEventHandler implements ApplicationListener<NettyEvent> {

    private static final Log logger = LogFactory.get(NettyEventHandler.class);
    private final BotService botService;
    private final UserService userService;

    public static boolean eventProcessing = false;

//    @Override
//    public void onApplicationEvent(NettyEvent event) {
//        SocketTask task = event.getTask();
//        JSONObject response = new JSONObject();
//        response.set("ok","ok");
//        task.getCtx().writeAndFlush(response);
//    }

    @Override
    public void onApplicationEvent(NettyEvent event) {
        eventProcessing = true;
        SocketTask task = event.getTask();
        JSONObject data = task.getData();
        ChannelHandlerContext ctx = task.getCtx();
        Argument argument = data.getBean("arguments", Argument.class);
        String message_id = data.getStr("message_id");
        String platformId = argument.getKookId();
        Integer osuId = userService.getOsuIdByByPlatformId(platformId);
        String result = null;
        try {
            switch (RequestType.valueOf(data.getStr("request_type"))) {
                case BP:
                    result = botService.getBP(Integer.parseInt(argument.getOsuId()), Integer.parseInt(argument.getSubArgument().get(0)), argument.getMode());
                    break;
                case BPME:
                    result = botService.getBP(osuId, Integer.parseInt(argument.getSubArgument().get(0)), argument.getMode());
                    break;
                case STAT:
                    result = botService.getStat(Integer.parseInt(argument.getOsuId()), argument.getMode());
                    break;
                case STATME:
                    result = botService.getStat(osuId, argument.getMode());
                    break;
                case RECENT:
                    result = botService.getRecent(osuId, argument.getMode());
                    break;
                case ERROR:
                default:
                    //TODO error tpye
                    break;
            }
        }catch (Throwable t){
            logger.error("BotServices异常:{}",t.getLocalizedMessage());
            t.printStackTrace();
        }
        JSONObject response = new JSONObject();
        response.set("message_id", message_id);
        response.set("request_type", data.getStr("request_type"));
        if (result != null) {
            response.set("success", true);
            response.set("base64", result);
        } else {
            response.set("success", false);
            response.set("msg", "idk");
        }
        eventProcessing = false;
        task.getCtx().writeAndFlush(response);
    }
}
