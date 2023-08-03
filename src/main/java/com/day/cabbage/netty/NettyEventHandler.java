package com.day.cabbage.netty;

import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.day.cabbage.constant.MessageResponse;
import com.day.cabbage.enums.RequestType;
import com.day.cabbage.mapper.CurrentMapper;
import com.day.cabbage.pojo.Argument;
import com.day.cabbage.pojo.NettyEvent;
import com.day.cabbage.pojo.osu.OsuUser;
import com.day.cabbage.services.BotService;
import com.day.cabbage.services.UserService;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class NettyEventHandler implements ApplicationListener<NettyEvent> {

    private static final Log logger = LogFactory.get(NettyEventHandler.class);
    private final BotService botService;
    private final UserService userService;
    private final CurrentMapper currentMapper;
    public static boolean eventProcessing = false;


    @Override
    public void onApplicationEvent(NettyEvent event) {
        eventProcessing = true;
        SocketTask task = event.getTask();
        JSONObject data = task.getData();
        Argument argument = data.getBean("arguments", Argument.class);
        String message_id = data.getStr("message_id");
        String platformId = argument.getPlatformId();
        JSONObject response = new JSONObject();
        response.set("message_id", message_id);
        response.set("request_type", data.getStr("request_type"));
        String result = null;
        Integer osuId;
        try {
            switch (RequestType.valueOf(data.getStr("request_type"))) {
                case BP:
                    result = botService.getBP(argument.getOsuId(), Integer.parseInt(argument.getSubArgument().get(0)), argument.getMode());
                    break;
                case BPME:
                    osuId = currentMapper.getCurrentOsuId(platformId);
                    if(osuId==null){
                        response.set("success",false);
                        response.set("msg", MessageResponse.NO_BIND);
                        eventProcessing=false;
                        task.getCtx().writeAndFlush(response);
                        return;
                    }
                    result = botService.getBP(osuId, Integer.parseInt(argument.getSubArgument().get(0)), argument.getMode());
                    break;
                case STAT:
                    result = botService.getStat(argument.getOsuId(), argument.getMode());
                    break;
                case STATME:
                    osuId = currentMapper.getCurrentOsuId(platformId);
                    if(osuId==null){
                        response.set("success",false);
                        response.set("msg", MessageResponse.NO_BIND);
                        eventProcessing=false;
                        task.getCtx().writeAndFlush(response);
                        return;
                    }
                    //TODO days?
                    result = botService.getStat(osuId, argument.getMode());
                    break;
                case RECENT:
                    osuId = currentMapper.getCurrentOsuId(platformId);
                    if(osuId==null){
                        response.set("success",false);
                        response.set("msg", MessageResponse.NO_BIND);
                        eventProcessing=false;
                        task.getCtx().writeAndFlush(response);
                    }
                    result = botService.getRecent(osuId, argument.getMode());
                    break;
                case BIND:
                    if(userService.isExit(argument.getOsuId())){
                        response.set("success", false);
                        response.set("msg",MessageResponse.ALREADY_BIND);
                    }else {
                        Integer mode = argument.getMode();
                        if(mode==null) mode = 0;
                        if(botService.bindUser(mode,platformId,argument.getOsuId())){
                            response.set("success", true);
                            response.set("msg",MessageResponse.OPERATION_SUCCESS);
                        }else {
                            response.set("success", false);
                            response.set("msg",MessageResponse.SYSTEM_ERROR);
                        }
                    }
                    eventProcessing = false;
                    task.getCtx().writeAndFlush(response);
                    return;
                case GETBINDLIST:
                    List<OsuUser> list = botService.getBindUser(platformId);
                    response.set("success", true);
                    response.set("list",list);
                    eventProcessing = false;
                    task.getCtx().writeAndFlush(response);
                    return;
                case ERROR:
                default:
                    //TODO error tpye
                    response.set("success", false);
                    response.set("msg","错误的命令");
                    eventProcessing = false;
                    task.getCtx().writeAndFlush(response);
                    break;
            }
        }catch (Throwable t){
            logger.error("BotServices异常:{}",t.getLocalizedMessage());
            t.printStackTrace();
        }
        if (result != null) {
            response.set("success", true);
            response.set("base64", result);
        } else {
            response.set("success", false);
            response.set("msg", MessageResponse.SYSTEM_ERROR);
        }
        eventProcessing = false;
        task.getCtx().writeAndFlush(response);
    }
}
