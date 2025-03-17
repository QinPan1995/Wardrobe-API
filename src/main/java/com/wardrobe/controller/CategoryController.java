package com.wardrobe.controller;

import com.wardrobe.common.Result;
import com.wardrobe.model.entity.Category;
import com.wardrobe.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public Result<List<Category>> getAllCategories() {
        return Result.success(categoryService.getAllCategories());
    }

    @PostMapping
    public Result<Category> addCategory(@RequestBody Category category) {
        return Result.success(categoryService.addCategory(category));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success(null);
    }
} 