package com.wardrobe.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wardrobe.common.Result;
import com.wardrobe.model.dto.ItemDTO;
import com.wardrobe.model.entity.Item;
import com.wardrobe.model.vo.ItemDetailVO;
import com.wardrobe.model.vo.ItemMainVO;
import com.wardrobe.service.ItemService;
import com.wardrobe.service.ItemImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemImageService itemImageService;

    @PostMapping("/page")
    public Result<Page<ItemMainVO>> getItem(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String season) {
        return Result.success(itemService.getItem(page, size, category, season));
    }

    @PostMapping
    public Result<Item> addItem(@RequestBody ItemDTO itemDTO) {
        return Result.success(itemService.addItem(itemDTO));
    }

    @GetMapping("/{id}")
    public Result<ItemDetailVO> getItem(@PathVariable Long id) {
        return Result.success(itemService.getItem(id));
    }

    @PutMapping("/{id}")
    public Result<Item> updateItem(
            @PathVariable Long id,
            @RequestBody ItemDTO itemDTO) {
        return Result.success(itemService.updateItem(id, itemDTO));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return Result.success(null);
    }

    @PostMapping("/upload")
    public Result<Long> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return Result.success(itemImageService.uploadImage(file));
    }
} 