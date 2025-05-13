package com.wardrobe.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wardrobe.common.Result;
import com.wardrobe.model.dto.ClothesDTO;
import com.wardrobe.model.entity.Clothes;
import com.wardrobe.model.vo.ClothesDetailVO;
import com.wardrobe.model.vo.ClothesMainVO;
import com.wardrobe.service.ClothesService;
import com.wardrobe.service.FileService;
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
    private FileService fileService;

    @PostMapping("/page")
    public Result<Page<ClothesMainVO>> getClothes(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String season) {
        return Result.success(clothesService.getClothes(page, size, category, season));
    }

    @PostMapping
    public Result<Clothes> addClothes(@RequestBody ClothesDTO clothesDTO) {
        return Result.success(clothesService.addClothes(clothesDTO));
    }

    @GetMapping("/{id}")
    public Result<ClothesDetailVO> getClothes(@PathVariable Long id) {
        return Result.success(clothesService.getClothes(id));
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
    public Result<Long> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return Result.success(fileService.uploadFile(file));
    }
} 