package com.wardrobe.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("file")
public class WardrobeFile extends BaseEntity{
    private Long userId;
    private String name;
    private Long size;
    private String type;
    private String url;
} 