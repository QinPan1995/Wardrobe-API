package com.wardrobe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wardrobe.model.entity.Item;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ItemMapper extends BaseMapper<Item> {
} 