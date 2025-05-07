package com.wardrobe.service.impl;

import com.wardrobe.model.entity.Category;
import com.wardrobe.model.entity.User;
import com.wardrobe.model.vo.ClothesMainVo;
import com.wardrobe.model.vo.WardrobeItemVo;
import com.wardrobe.model.vo.WardrobeVo;
import com.wardrobe.service.CategoryService;
import com.wardrobe.service.ClothesService;
import com.wardrobe.service.UserService;
import com.wardrobe.service.WardrobeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
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
    public List<WardrobeVo> allWardrobe() {
        List<WardrobeVo> wardrobeVos = new ArrayList<>();
        //获取所有分类
        List<Category> allCategories = categoryService.getAllCategories();
        if (CollectionUtils.isEmpty(allCategories)){
            return wardrobeVos;
        }
        //获取
        User currentUser = userService.getCurrentUser();
        //获取相关衣服
        List<ClothesMainVo> clothesMainVos = clothesService.allClotheByUserId(currentUser.getId());
        for (Category category : allCategories) {
            WardrobeVo wardrobeVo = new WardrobeVo();
            wardrobeVo.setTitle(category.getName());
            wardrobeVo.setIcon("");
            wardrobeVo.setType(category.getName());

            List<ClothesMainVo> clothesMainVoList = clothesMainVos.stream().filter(o -> category.getName().equals(o.getCategory())).collect(Collectors.toList());
            List<WardrobeItemVo> wardrobes = new ArrayList<>();
            for (ClothesMainVo clothesMainVo : clothesMainVoList) {
                WardrobeItemVo wardrobeItemVo = new WardrobeItemVo();
                List<String> images = clothesMainVo.getImages();
                if (CollectionUtils.isEmpty(images)){
                    continue;
                }
                wardrobeItemVo.setImage(images.get(0));
                wardrobes.add(wardrobeItemVo);
            }
            wardrobeVo.setWardrobes(wardrobes);
            wardrobeVo.setCount(clothesMainVoList.size());
            wardrobeVos.add(wardrobeVo);
        }
        return wardrobeVos;
    }
}
