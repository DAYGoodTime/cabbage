package com.day.cabbage.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("resource")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resource {
    @TableId("Id")
    private int Id;
    private String name;
    private byte[] data;
}
