package com.day.cabbage.pojo.osu;

import cn.hutool.core.annotation.Alias;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("userinfo")
public class Userinfo {
    /**
     * 这个字段不写入数据库
     */
    @Alias("username")
    @TableField(exist = false)
    private String userName;
    private Integer mode;
    private int userId;
    private int count300;
    private int count100;
    private int count50;
    @Alias("playcount")
    @TableField(value = "playcount")
    private int playCount;
    private float accuracy;
    private float ppRaw;
    private long rankedScore;
    private long totalScore;
    private float level;
    private int ppRank;
    private int countRankSs;
    @TableField(exist = false)
    private int countRankSsh;
    private int countRankS;
    @TableField(exist = false)
    private int countRankSh;
    private int countRankA;

    private LocalDate queryDate;

}
