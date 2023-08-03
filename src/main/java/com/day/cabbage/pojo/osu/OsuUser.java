package com.day.cabbage.pojo.osu;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("binding_table")
public class OsuUser {
    @TableId
    private String platformId;
    private Integer osuId;
    private String role;
    private String legacyName;
    private String currentName;
    @TableField("is_banned")
    private boolean banned;
    private Integer mainMode;
    private LocalDate lastActiveDate;
}
