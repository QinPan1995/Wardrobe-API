package com.wardrobe.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("clothes")
public class Clothes {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private String category;
    private String season;
    private String color;
    private String imageUrl;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 