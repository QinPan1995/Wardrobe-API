package com.wardrobe.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wardrobe.model.dto.ClothesDTO;
import com.wardrobe.model.entity.Clothes;
import com.wardrobe.model.vo.ClothesMainVo;

import java.util.List;

public interface ClothesService extends IService<Clothes> {
    Clothes addClothes(ClothesDTO clothesDTO);
    Page<ClothesMainVo> getClothes(Integer page, Integer size, String category, String season);
    Clothes updateClothes(Long id, ClothesDTO clothesDTO);
    void deleteClothes(Long id);

    List<ClothesMainVo> clothesMains(List<Clothes> records);

    List<ClothesMainVo> allClotheByUserId(Long userId);
} 