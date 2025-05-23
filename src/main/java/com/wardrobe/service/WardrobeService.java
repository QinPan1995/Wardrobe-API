package com.wardrobe.service;

import com.wardrobe.model.vo.WardrobeStatsVO;
import com.wardrobe.model.vo.WardrobeVO;

import java.util.List;

/**
 * @author ：luke
 * @date ：Created in 2025/5/6 15:17
 * @description：衣柜服务
 * @modified By：
 */


public interface WardrobeService {
    List<WardrobeVO> allWardrobe();

    List<WardrobeStatsVO> stats();
}
