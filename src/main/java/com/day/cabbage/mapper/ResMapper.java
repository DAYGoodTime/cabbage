package com.day.cabbage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.day.cabbage.pojo.Resource;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ResMapper extends BaseMapper<Resource> {

    @Insert("INSERT INTO `osufile` VALUES (null,#{bid},#{data})")
    Integer addOsuFile(@Param("bid") Integer bid, @Param("data") String data);

    @Select("SELECT `data` FROM osufile WHERE `bid` = #{bid} ")
    String getOsuFileBybid(@Param("bid") Integer bid);

    @Insert("INSERT INTO `bgfile` VALUES (null,#{sid},#{name},#{data})")
    Integer addBG(@Param("sid") Integer sid, @Param("name") String name, @Param("data") byte[] data);

    @Select("SELECT `data` FROM `bgfile` WHERE `sid` = #{sid} AND `name` = #{name} ")
        //这里似乎不能用byte[]？
    Object getBGBySidAndName(@Param("sid") Integer sid, @Param("name") String name);

    @Select("SELECT `name`,`data` FROM `resource`")
    List<Map<String, Object>> getImages();

    @Select("SELECT `data` FROM `resource` WHERE `name` = #{name}")
    Object getImage(@Param("name") String name);


}
