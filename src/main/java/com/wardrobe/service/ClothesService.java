package com.wardrobe.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wardrobe.model.dto.ClothesDTO;
import com.wardrobe.model.entity.Clothes;
import com.wardrobe.model.vo.ClothesDetailVO;
import com.wardrobe.model.vo.ClothesMainVO;

import java.util.List;

public interface ClothesService extends IService<Clothes> {
    Clothes addClothes(ClothesDTO clothesDTO);
    ClothesDetailVO getClothes(Long id);
    Page<ClothesMainVO> getClothes(Integer page, Integer size, String category, String season);
    Clothes updateClothes(Long id, ClothesDTO clothesDTO);
    void deleteClothes(Long id);

    List<ClothesMainVO> clothesMains(List<Clothes> records);

    List<ClothesMainVO> allClotheByUserId(Long userId);
} 