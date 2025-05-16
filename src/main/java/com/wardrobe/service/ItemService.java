package com.wardrobe.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wardrobe.model.dto.ItemDTO;
import com.wardrobe.model.entity.Item;
import com.wardrobe.model.vo.ItemDetailVO;
import com.wardrobe.model.vo.ItemMainVO;

import java.util.List;

public interface ItemService extends IService<Item> {
    Item addItem(ItemDTO itemDTO);
    ItemDetailVO getItem(Long id);
    Page<ItemMainVO> getItem(Integer page, Integer size, String category, String season);
    Item updateItem(Long id, ItemDTO itemDTO);
    void deleteItem(Long id);

    List<ItemMainVO> itemMains(List<Item> records);

    List<ItemMainVO> allClotheByUserId(Long userId);
} 