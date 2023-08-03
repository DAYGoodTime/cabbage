package com.day.cabbage.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("userrole")
public class User {
    private Integer userId;
    private String role;
    private String platformId;
    private String legacyUname;
    private String currentUname;
    @TableField("is_banned")
    private boolean banned;
    private Integer mode;
    private Long repeatCount;
    private Long speakingCount;
    private String mainRole;
    private Boolean useEloBorder;
    private LocalDate lastActiveDate;
}
