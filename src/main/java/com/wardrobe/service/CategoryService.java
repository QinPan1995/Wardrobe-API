package com.wardrobe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wardrobe.model.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    List<Category> getAllCategories();
    Category addCategory(Category category);
    void deleteCategory(Long id);
} 