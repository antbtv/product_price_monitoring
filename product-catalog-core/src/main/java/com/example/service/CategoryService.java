package com.example.service;

import com.example.entity.Category;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryService {

    void createCategory(Category category);

    Category getCategoryById(int id);

    void updateCategory(Category category);

    void deleteCategory(int id);

    List<Category> getAllCategories();
}
