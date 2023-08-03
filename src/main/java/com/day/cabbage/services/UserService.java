package com.day.cabbage.services;

import com.day.cabbage.pojo.User;
import okhttp3.internal.platform.Platform;

public interface UserService {

    User getUser(Integer uid);

    User registerUser(Integer userId, Integer mode, String platformId, String role);

    void updateUser(User user);

    Integer getOsuIdByByPlatformId(String platformId);
}
