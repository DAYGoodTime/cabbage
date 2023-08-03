package com.day.cabbage.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CurrentMapper {

    @Select("select current_select.ous_id from  current_select where platform_id=#{platform_id} ")
    Integer getCurrentOsuId(String platform_id);

    @Insert("insert into  current_select (platform_id,ous_id) values (#{platform_id},#{osu_id})  ")
    Integer addCurrent(String platform_id,Integer osu_id);

    @Update("update current_select SET current_select.ous_id=#{osu_id} where platform_id=#{platform_id} ")
    Integer updateCurrent(String platform_id,Integer osu_id);
}
