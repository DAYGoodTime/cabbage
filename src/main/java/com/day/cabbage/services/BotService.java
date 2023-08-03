package com.day.cabbage.services;

import com.day.cabbage.pojo.osu.OsuUser;

import java.util.List;

public interface BotService {

    boolean bindUser(Integer mode,String platformId,Integer osuId);

    String getStat(Integer uid,Integer mode);

    String getBP(Integer uid,Integer num,Integer mode);

    String getRecent(Integer uid,Integer mode);

    List<OsuUser> getBindUser(String platformId);
}
