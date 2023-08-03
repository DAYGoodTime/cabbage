package com.day.cabbage.services;

import com.day.cabbage.pojo.User;
import com.day.cabbage.pojo.osu.OsuUser;
import okhttp3.internal.platform.Platform;

public interface UserService {

    OsuUser getUser(Integer ousId);

    boolean registerUser(OsuUser osuUser);

    void updateUser(User user);

    boolean isExit(Integer platformId);

}
