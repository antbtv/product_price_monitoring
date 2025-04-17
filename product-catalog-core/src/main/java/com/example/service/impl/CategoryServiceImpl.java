package com.example.service.impl;

import com.example.dao.CategoryDao;
import com.example.entity.Category;
import com.example.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao;

    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Transactional
    @Override
    public void createCategory(Category category) {
        categoryDao.create(category);
    }

    @Transactional(readOnly = true)
    @Override
    public Category getCategoryById(int id) {
        return categoryDao.findById(id);
    }

    @Transactional
    @Override
    public void updateCategory(Category category) {
        categoryDao.update(category);
    }

    @Transactional
    @Override
    public void deleteCategory(int id) {
        categoryDao.delete(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }
}
