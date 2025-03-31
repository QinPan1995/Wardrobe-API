package com.wardrobe.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wardrobe.common.FileUtil;
import com.wardrobe.common.Result;
import com.wardrobe.model.dto.ClothesDTO;
import com.wardrobe.model.entity.Clothes;
import com.wardrobe.service.ClothesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/clothes")
public class ClothesController {

    @Autowired
    private ClothesService clothesService;

    @Autowired
    private FileUtil fileUtil;

    @PostMapping
    public Result<Clothes> addClothes(@RequestBody ClothesDTO clothesDTO) {
        return Result.success(clothesService.addClothes(clothesDTO));
    }

    @GetMapping
    public Result<Page<Clothes>> getClothes(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String season) {
        return Result.success(clothesService.getClothes(page, size, category, season));
    }

    @PutMapping("/{id}")
    public Result<Clothes> updateClothes(
            @PathVariable Long id,
            @RequestBody ClothesDTO clothesDTO) {
        return Result.success(clothesService.updateClothes(id, clothesDTO));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteClothes(@PathVariable Long id) {
        clothesService.deleteClothes(id);
        return Result.success(null);
    }

    @PostMapping("/upload")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String imageUrl = fileUtil.uploadImage(file);
        return Result.success(imageUrl);
    }
} 