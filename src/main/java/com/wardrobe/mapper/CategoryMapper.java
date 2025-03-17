package com.wardrobe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wardrobe.model.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
} 