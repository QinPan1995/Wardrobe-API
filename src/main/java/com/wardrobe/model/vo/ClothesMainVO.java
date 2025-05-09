package com.wardrobe.model.vo;

import com.wardrobe.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author ：luke
 * @date ：Created in 2025/4/30 16:10
 * @description：
 * @modified By：
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class ClothesMainVO extends BaseEntity {

    /**
     * 图片列表
     */
    private List<String> images;
    /**
     * 衣物类型（单选）
     * 示例值：上衣, 裤子, 裙子, 外套, 连衣裙, 鞋子, 配饰等
     */
    private String category;
}
