package com.wardrobe.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("item")
public class Item extends BaseEntity{
    private Long userId;
    /**
     * 物品名称（例如：白衬衫、牛仔裤）
     */
    private String name;

    /**
     * 物品类型（单选）
     * 示例值：上衣, 裤子, 裙子, 外套, 连衣裙, 鞋子, 配饰等
     */
    private String category;

    /**
     * 适用季节（多选）
     * 示例值：春, 夏, 秋, 冬
     * 可用逗号分隔存储多个季节，如："春夏"
     */
    private String season;

    /**
     * 适用场合（单选）
     * 示例值：日常, 正式, 运动, 派对, 工作等
     */
    private String occasion;

    /**
     * 品牌名称（例如：Nike, Zara, Uniqlo）
     */
    private String brand;

    /**
     * 价格（使用 BigDecimal 避免精度丢失）
     */
    private BigDecimal price;

    /**
     * 购买日期（格式：yyyy-MM-dd）
     */
    private LocalDate purchaseDate;

    /**
     * 存放位置（例如：衣柜A-第2层, 抽屉3）
     */
    private String storageLocation;

    /**
     * 备注信息（可选，用于记录其他额外信息）
     */
    private String remark;
} 