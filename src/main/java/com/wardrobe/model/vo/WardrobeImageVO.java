package com.wardrobe.model.vo;

import com.wardrobe.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ：luke
 * @date ：Created in 2025/5/9 18:20
 * @description：衣柜图片
 * @modified By：
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class WardrobeImageVO extends BaseEntity {
    private String url;
}
