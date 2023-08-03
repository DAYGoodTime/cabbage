package com.day.cabbage.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.day.cabbage.constant.CabbageConfig;
import com.day.cabbage.manager.ApiManager;
import com.day.cabbage.mapper.UserInfoMapper;
import com.day.cabbage.mapper.UserMapper;
import com.day.cabbage.pojo.User;
import com.day.cabbage.pojo.osu.Userinfo;
import com.day.cabbage.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@RestControllerAdvice
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final ApiManager apiManager;

    private final UserInfoMapper userInfoMapper;

    @Override
    public User getUser(Integer uid) {
        QueryWrapper<User> filter = new QueryWrapper<>();
        filter.eq("user_id", uid);
        return userMapper.selectOne(filter);
    }

    @Override
    public User registerUser(Integer userId, Integer mode, String platformId, String role) {
        //构造User对象写入数据库，如果指定了mode就使用指定mode
        Userinfo userFromAPI = null;
        for (int i = 0; i < 4; i++) {
            userFromAPI = apiManager.getUser(i, userId);
            if (LocalTime.now().isAfter(LocalTime.of(4, 0))) {
                userFromAPI.setQueryDate(LocalDate.now());
            } else {
                userFromAPI.setQueryDate(LocalDate.now().minusDays(1));
            }
            //TODO RedisDb insert
            userInfoMapper.insert(userFromAPI);
        }
        User user = new User(userId, role, platformId, "[]", userFromAPI.getUserName(), false, mode, 0L, 0L, CabbageConfig.DEFAULT_ROLE, false, LocalDate.now());
        userMapper.insert(user);
        return user;
    }

    @Override
    public void updateUser(User user) {
        //TODO update
    }

    @Override
    public Integer getOsuIdByByPlatformId(String platformId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("platform_id",platformId);
        User user = userMapper.selectOne(queryWrapper);
        Integer res = null;
        if(user!=null) res = user.getUserId();
        return res;
    }
}
