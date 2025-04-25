package com.example.service.impl;

import com.example.dao.CategoryDao;
import com.example.dto.CategoryDTO;
import com.example.dto.PriceDTO;
import com.example.entity.Category;
import com.example.entity.Price;
import com.example.mapper.CategoryMapper;
import com.example.mapper.PriceMapper;
import com.example.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
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
    public Category getCategoryById(Long id) {
        return categoryDao.findById(id);
    }

    @Transactional
    @Override
    public void updateCategory(Category category) {
        categoryDao.update(category);
    }

    @Transactional
    @Override
    public void deleteCategory(Long id) {
        categoryDao.delete(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }

    @Transactional(readOnly = true)
    public void exportCategoriesToJson(String filePath) throws IOException {
        List<Category> categories = categoryDao.findAll();
        List<CategoryDTO> categoryDTOS = CategoryMapper.INSTANCE.toDtoList(categories);

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        objectMapper.writeValue(new File(filePath), categoryDTOS);
    }
}
