package com.wardrobe.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("item_image_ref")
public class ItemImageRef extends BaseEntity {
    private Long itemId;
    private Long itemImageId;
} 