package com.day.cabbage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.day.cabbage.pojo.User;
import com.day.cabbage.pojo.osu.OsuUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<OsuUser> {

}
