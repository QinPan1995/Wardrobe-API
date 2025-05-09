package com.wardrobe.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author ：luke
 * @date ：Created in 2025/5/6 15:20
 * @description：衣柜数据
 * @modified By：
 */

@Data
public class WardrobeVO {

    private String title; // 分类标题
    private String icon; // 分类图标类名
    private int count; // 衣物数量
    private String type; // 分类类型
    private List<WardrobeItemVO> wardrobes; // 衣柜明细
}
