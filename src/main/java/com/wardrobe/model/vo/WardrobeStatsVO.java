package com.wardrobe.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author ：luke
 * @date ：Created in 2025/5/6 15:20
 * @description：衣柜数据
 * @modified By：
 */

@Data
public class WardrobeStatsVO {

    private String title; // 分类标题
    private String icon; // 分类图标类名
    private int totalCount; // 总数量
    private String type; // 分类类型
    private BigDecimal totalAmount;//总价值
    private BigDecimal avgAmount;//平均单价
    private BigDecimal maxAmount;//最贵宝贝
    private BigDecimal minAmount;//最惠宝贝
}
