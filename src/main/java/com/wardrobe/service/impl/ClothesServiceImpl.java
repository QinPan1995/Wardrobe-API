package com.wardrobe.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wardrobe.common.exception.BusinessException;
import com.wardrobe.mapper.ClothesMapper;
import com.wardrobe.model.dto.ClothesDTO;
import com.wardrobe.model.entity.Clothes;
import com.wardrobe.model.entity.User;
import com.wardrobe.service.ClothesService;
import com.wardrobe.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ClothesServiceImpl extends ServiceImpl<ClothesMapper, Clothes> implements ClothesService {

    @Autowired
    private UserService userService;

    @Override
    public Clothes addClothes(ClothesDTO clothesDTO) {
        // 参数校验
        if (!StringUtils.hasText(clothesDTO.getName())) {
            throw new BusinessException("衣物名称不能为空");
        }
        if (!StringUtils.hasText(clothesDTO.getCategory())) {
            throw new BusinessException("衣物分类不能为空");
        }
        if (!StringUtils.hasText(clothesDTO.getSeason())) {
            throw new BusinessException("适用季节不能为空");
        }

        User currentUser = userService.getCurrentUser();
        
        Clothes clothes = new Clothes();
        BeanUtils.copyProperties(clothesDTO, clothes);
        clothes.setUserId(currentUser.getId());
        
        save(clothes);
        return clothes;
    }

    @Override
    public Page<Clothes> getClothes(Integer page, Integer size, String category, String season) {
        User currentUser = userService.getCurrentUser();
        
        Page<Clothes> pageParam = new Page<>(page, size);
        
        return lambdaQuery()
                .eq(Clothes::getUserId, currentUser.getId())
                .eq(StringUtils.hasText(category), Clothes::getCategory, category)
                .eq(StringUtils.hasText(season), Clothes::getSeason, season)
                .orderByDesc(Clothes::getCreateTime)
                .page(pageParam);
    }

    @Override
    public Clothes updateClothes(Long id, ClothesDTO clothesDTO) {
        // 参数校验
        if (id == null) {
            throw new BusinessException("衣物ID不能为空");
        }
        
        User currentUser = userService.getCurrentUser();
        
        Clothes clothes = lambdaQuery()
                .eq(Clothes::getId, id)
                .eq(Clothes::getUserId, currentUser.getId())
                .one();
                
        if (clothes == null) {
            throw new BusinessException("衣物不存在或无权限修改");
        }
        
        if (StringUtils.hasText(clothesDTO.getName())) {
            clothes.setName(clothesDTO.getName());
        }
        if (StringUtils.hasText(clothesDTO.getCategory())) {
            clothes.setCategory(clothesDTO.getCategory());
        }
        if (StringUtils.hasText(clothesDTO.getSeason())) {
            clothes.setSeason(clothesDTO.getSeason());
        }
        if (StringUtils.hasText(clothesDTO.getColor())) {
            clothes.setColor(clothesDTO.getColor());
        }
        if (StringUtils.hasText(clothesDTO.getImageUrl())) {
            clothes.setImageUrl(clothesDTO.getImageUrl());
        }
        if (StringUtils.hasText(clothesDTO.getDescription())) {
            clothes.setDescription(clothesDTO.getDescription());
        }
        
        updateById(clothes);
        return clothes;
    }

    @Override
    public void deleteClothes(Long id) {
        if (id == null) {
            throw new BusinessException("衣物ID不能为空");
        }
        
        User currentUser = userService.getCurrentUser();
        
        boolean success = lambdaUpdate()
                .eq(Clothes::getId, id)
                .eq(Clothes::getUserId, currentUser.getId())
                .remove();
                
        if (!success) {
            throw new BusinessException("衣物不存在或无权限删除");
        }
    }
} 