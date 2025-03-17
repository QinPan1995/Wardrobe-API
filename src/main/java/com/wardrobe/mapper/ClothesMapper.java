package com.wardrobe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wardrobe.model.entity.Clothes;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClothesMapper extends BaseMapper<Clothes> {
} 