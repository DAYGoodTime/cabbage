package com.day.cabbage.services.impl;

import com.day.cabbage.mapper.UserMapper;
import com.day.cabbage.pojo.User;
import com.day.cabbage.pojo.osu.OsuUser;
import com.day.cabbage.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Service
@RequiredArgsConstructor
@RestControllerAdvice
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public OsuUser getUser(Integer osuId) {
        return userMapper.selectById(osuId);
    }

    @Override
    public boolean registerUser(OsuUser osuUser) {
        return userMapper.insert(osuUser) == 1;
    }

    @Override
    public void updateUser(User user) {
        //TODO update
    }

    @Override
    public boolean isExit(Integer osuId) {
        return userMapper.selectById(osuId) != null;
    }

}
