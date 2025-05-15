package com.wardrobe.service.impl;

import com.wardrobe.model.entity.Category;
import com.wardrobe.model.entity.User;
import com.wardrobe.model.vo.ClothesMainVO;
import com.wardrobe.model.vo.WardrobeItemVO;
import com.wardrobe.model.vo.WardrobeStatsVO;
import com.wardrobe.model.vo.WardrobeVO;
import com.wardrobe.service.CategoryService;
import com.wardrobe.service.ClothesService;
import com.wardrobe.service.UserService;
import com.wardrobe.service.WardrobeService;
import com.wardrobe.util.OrikaUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author ：luke
 * @date ：Created in 2025/5/6 15:17
 * @description：衣柜服务
 * @modified By：
 */

@Service
public class WardrobeServiceImpl implements WardrobeService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private ClothesService clothesService;

    @Override
    public List<WardrobeVO> allWardrobe() {
        List<WardrobeVO> wardrobeVOS = new ArrayList<>();
        //获取所有分类
        List<Category> allCategories = categoryService.getAllCategories();
        if (CollectionUtils.isEmpty(allCategories)) {
            return wardrobeVOS;
        }
        //获取
        User currentUser = userService.getCurrentUser();
        //获取相关衣服
        List<ClothesMainVO> clothesMainVOS = clothesService.allClotheByUserId(currentUser.getId());
        for (Category category : allCategories) {
            WardrobeVO wardrobeVo = new WardrobeVO();
            wardrobeVo.setTitle(category.getName());
            wardrobeVo.setIcon("");
            wardrobeVo.setType(category.getName());

            List<ClothesMainVO> clothesMainVOList = clothesMainVOS.stream().filter(o -> category.getName().equals(o.getCategory())).collect(Collectors.toList());
            List<WardrobeItemVO> wardrobes = new ArrayList<>();
            for (ClothesMainVO clothesMainVo : clothesMainVOList) {
                List<String> images = clothesMainVo.getImages();
                String image;
                if (CollectionUtils.isEmpty(images)) {
                    //默认图片
                    image = "http://localhost:8080/upload/default.svg";
                } else {
                    image = images.get(0);
                }
                WardrobeItemVO wardrobeItemVo = OrikaUtil.convert(clothesMainVo, WardrobeItemVO.class);
                wardrobeItemVo.setImage(image);
                wardrobes.add(wardrobeItemVo);
            }
            wardrobeVo.setWardrobes(wardrobes);
            wardrobeVo.setCount(clothesMainVOList.size());
            wardrobeVOS.add(wardrobeVo);
        }
        return wardrobeVOS;
    }

    @Override
    public List<WardrobeStatsVO> stats() {
        List<WardrobeVO> wardrobeVOS = allWardrobe();
        List<WardrobeStatsVO> wardrobeStatsVOS = new ArrayList<>();
        for (WardrobeVO wardrobeVO : wardrobeVOS) {
            WardrobeStatsVO wardrobeStatsVO = new WardrobeStatsVO();
            wardrobeStatsVO.setTitle(wardrobeVO.getTitle());
            wardrobeStatsVO.setIcon(wardrobeVO.getIcon());
            wardrobeStatsVO.setType(wardrobeVO.getType());
            List<WardrobeItemVO> wardrobes = wardrobeVO.getWardrobes();
            wardrobeStatsVO.setTotalCount(getTotalCount(wardrobes));
            wardrobeStatsVO.setTotalAmount(geTotalAmount(wardrobes));
            wardrobeStatsVO.setAvgAmount(getAvgAmount(wardrobes));
            wardrobeStatsVO.setMaxAmount(getMaxAmount(wardrobes));
            wardrobeStatsVO.setMinAmount(getMinAmount(wardrobes));
            wardrobeStatsVOS.add(wardrobeStatsVO);
        }
        return wardrobeStatsVOS;
    }

    /**
     * 获取总数量
     *
     * @param wardrobes
     * @return
     */
    private int getTotalCount(List<WardrobeItemVO> wardrobes) {
        if (CollectionUtils.isEmpty(wardrobes)) {
            return 0;
        }
        return wardrobes.size();
    }

    /**
     * 获取总价值
     */
    private BigDecimal geTotalAmount(List<WardrobeItemVO> wardrobes) {
        if (CollectionUtils.isEmpty(wardrobes)) {
            return BigDecimal.ZERO;
        }
        return wardrobes.stream().map(WardrobeItemVO::getPrice).filter(Objects::nonNull).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    /**
     * 获取最大单价
     */
    private BigDecimal getMaxAmount(List<WardrobeItemVO> wardrobes) {
        if (CollectionUtils.isEmpty(wardrobes)) {
            return BigDecimal.ZERO;
        }
        //获取wardrobes中Price的最大值
        return wardrobes.stream().map(WardrobeItemVO::getPrice).filter(Objects::nonNull).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO); // 如果没有值，返回 0;
    }

    /**
     * 获取最小单价
     */
    private BigDecimal getMinAmount(List<WardrobeItemVO> wardrobes) {
        if (CollectionUtils.isEmpty(wardrobes)) {
            return BigDecimal.ZERO;
        }
        return wardrobes.stream().map(WardrobeItemVO::getPrice).filter(Objects::nonNull).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO); // 如果没有值，返回 0;
    }

    /**
     * 获取平均单价
     *
     * @param wardrobes
     * @return
     */
    private BigDecimal getAvgAmount(List<WardrobeItemVO> wardrobes) {
        if (CollectionUtils.isEmpty(wardrobes)) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalValue = geTotalAmount(wardrobes);
        int totalCount = getTotalCount(wardrobes);
        if (totalCount == 0) {
            return BigDecimal.ZERO;
        }
        return totalValue.divide(new BigDecimal(totalCount), 2, RoundingMode.HALF_UP);
    }
}
