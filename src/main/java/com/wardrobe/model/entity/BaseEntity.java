package com.wardrobe.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ：luke
 * @date ：Created in 2025/4/30 10:03
 * @description：
 * @modified By：
 */

@Data
public class BaseEntity {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 软删除标记
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
