package com.wardrobe.model.vo;

import com.wardrobe.model.entity.Clothes;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ：luke
 * @date ：Created in 2025/5/6 15:25
 * @description：衣柜明细
 * @modified By：
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class WardrobeItemVO extends Clothes {

    //图片
    private String image;
}
