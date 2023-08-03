package com.day.cabbage.services;

public interface BotService {

    String getStat(Integer uid,Integer mode);

    String getBP(Integer uid,Integer num,Integer mode);

    String getRecent(Integer uid,Integer mode);
}
