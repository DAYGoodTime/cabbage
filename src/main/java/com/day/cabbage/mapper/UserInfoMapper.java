package com.day.cabbage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.day.cabbage.pojo.osu.Userinfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface UserInfoMapper extends BaseMapper<Userinfo> {

    @Select("SELECT * , abs(UNIX_TIMESTAMP(query_date) - UNIX_TIMESTAMP(#{queryDate})) AS ds " +
            "FROM `userinfo`  WHERE `user_id` = #{userId} AND `mode` = #{mode} ORDER BY ds LIMIT 1")
    Userinfo getNearestUserInfo(@Param("mode") Integer mode, @Param("userId") Integer userId, @Param("queryDate") LocalDate queryDate);


}
