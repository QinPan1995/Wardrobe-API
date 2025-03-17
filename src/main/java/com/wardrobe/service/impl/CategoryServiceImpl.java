package com.wardrobe.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wardrobe.mapper.CategoryMapper;
import com.wardrobe.model.entity.Category;
import com.wardrobe.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getAllCategories() {
        return list();
    }

    @Override
    public Category addCategory(Category category) {
        save(category);
        return category;
    }

    @Override
    public void deleteCategory(Long id) {
        removeById(id);
    }
} 