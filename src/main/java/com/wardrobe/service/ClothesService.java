package com.wardrobe.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wardrobe.model.dto.ClothesDTO;
import com.wardrobe.model.entity.Clothes;
import com.wardrobe.model.vo.ClothesMainPage;

public interface ClothesService extends IService<Clothes> {
    Clothes addClothes(ClothesDTO clothesDTO);
    Page<ClothesMainPage> getClothes(Integer page, Integer size, String category, String season);
    Clothes updateClothes(Long id, ClothesDTO clothesDTO);
    void deleteClothes(Long id);
} 