package com.wardrobe.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("clothes_file")
public class ClothesFile extends BaseEntity {
    private Long clothesId;
    private Long fileId;
} 