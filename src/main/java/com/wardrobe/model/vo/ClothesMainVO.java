package com.wardrobe.model.vo;

import com.wardrobe.model.entity.Clothes;
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
public class ClothesMainVO extends Clothes {

    /**
     * 图片列表
     */
    private List<String> images;
}
