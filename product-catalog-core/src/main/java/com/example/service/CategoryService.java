package com.example.service;

import com.example.entity.Category;

import java.util.List;

public interface CategoryService {

    Category createCategory(Category category);

    Category getCategoryById(Long id);

    void updateCategory(Category category);

    void deleteCategory(Long id);

    List<Category> getAllCategories();
}
