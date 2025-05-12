package com.example.service.impl;

import com.example.MessageSources;
import com.example.dao.CategoryDao;
import com.example.dto.CategoryDTO;
import com.example.dto.StoreDTO;
import com.example.entity.Category;
import com.example.mapper.CategoryMapper;
import com.example.service.CategoryService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao;
    private static final Logger logger = LogManager.getLogger(CategoryServiceImpl.class);

    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Transactional
    @Override
    public Category createCategory(Category category) {
        try {
            return categoryDao.create(category);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_CREATE);
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Category getCategoryById(Long id) {
        try {
            return categoryDao.findById(id);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_ONE);
            return null;
        }
    }

    @Transactional
    @Override
    public void updateCategory(Category category) {
        try {
            categoryDao.update(category);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_UPDATE);
        }
    }

    @Transactional
    @Override
    public void deleteCategory(Long id) {
        try {
            categoryDao.delete(id);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_DELETE);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Category> getAllCategories() {
        try {
            return categoryDao.findAll();
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_MANY);
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] exportCategoriesToJson() throws IOException {
        List<Category> categories = categoryDao.findAll();
        List<CategoryDTO> categoryDTOS = CategoryMapper.INSTANCE.toDtoList(categories);

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        objectMapper.writeValue(outputStream, categoryDTOS);
        return outputStream.toByteArray();
    }

    @Transactional
    @Override
    public List<CategoryDTO> importCategoriesFromJson(byte[] data) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<CategoryDTO> categoryDTOS = objectMapper.readValue(
                data,
                new TypeReference<>() {
                }
        );

        List<Category> categories = CategoryMapper.INSTANCE.toEntityList(categoryDTOS);
        categories.forEach(categoryDao::create);

        return categoryDTOS;
    }
}
