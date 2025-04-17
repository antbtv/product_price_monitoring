package com.example.service.impl;

import com.example.dao.CategoryDao;
import com.example.entity.Category;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl {

    private final CategoryDao categoryDao;

    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Transactional
    public void createCategory(Category category) {
        categoryDao.create(category);
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(int id) {
        return categoryDao.findById(id);
    }

    @Transactional
    public void updateCategory(Category category) {
        categoryDao.update(category);
    }

    @Transactional
    public void deleteCategory(int id) {
        categoryDao.delete(id);
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }
}
