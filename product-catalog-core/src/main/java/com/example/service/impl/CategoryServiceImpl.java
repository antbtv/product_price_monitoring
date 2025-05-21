package com.example.service.impl;

import com.example.dao.CategoryDao;
import com.example.dto.CategoryDTO;
import com.example.entity.Category;
import com.example.exceptions.CategoryNotFoundException;
import com.example.exceptions.DataExportException;
import com.example.exceptions.DataImportException;
import com.example.mapper.CategoryMapper;
import com.example.service.CategoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao;
    private final CategoryMapper categoryMapper;
    private final ObjectMapper objectMapper;

    public CategoryServiceImpl(CategoryDao categoryDao,
                               CategoryMapper categoryMapper,
                               ObjectMapper objectMapper) {
        this.categoryDao = categoryDao;
        this.categoryMapper = categoryMapper;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @Override
    public Category createCategory(Category category) {
        Category createdCategory = categoryDao.create(category);
        log.info("Создана категория ID: {}, название: {}",
                createdCategory.getCategoryId(),
                createdCategory.getCategoryName());
        return createdCategory;
    }

    @Transactional(readOnly = true)
    @Override
    public Category getCategoryById(Long id) {
        Category category = categoryDao.findById(id);
        if (category == null) {
            log.info("Категория с ID {} не найдена", id);
            throw new CategoryNotFoundException(id);
        }
        log.info("Найдена категория ID: {}", id);
        return category;
    }

    @Transactional
    @Override
    public void updateCategory(Category category) {
        if (categoryDao.findById(category.getCategoryId()) == null) {
            throw new CategoryNotFoundException(category.getCategoryId());
        }
        categoryDao.update(category);
        log.info("Обновлена категория ID: {}", category.getCategoryId());
    }

    @Transactional
    @Override
    public void deleteCategory(Long id) {
        if (categoryDao.findById(id) == null) {
            throw new CategoryNotFoundException(id);
        }
        categoryDao.delete(id);
        log.info("Удалена категория ID: {}", id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = categoryDao.findAll();
        log.info("Найдено категорий: {}", categories.size());
        return categories;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Category> getAllCategoriesByParentId(Long parentId) {
        if (parentId != null && categoryDao.findById(parentId) == null) {
            log.warn("Попытка получить дочерние категории для несуществующего родителя ID: {}", parentId);
            throw new CategoryNotFoundException(parentId);
        }

        List<Category> categories = categoryDao.findAllByParentId(parentId);
        log.info("Найдено дочерних категорий для родителя ID {}: {}", parentId, categories.size());
        return categories;
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] exportCategoriesToJson() {
        try {
            List<CategoryDTO> dtos = categoryMapper.toDtoList(categoryDao.findAll());
            log.info("Экспортировано категорий: {}", dtos.size());
            return objectMapper.writeValueAsBytes(dtos);
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации категорий", e);
            throw new DataExportException("Ошибка экспорта категорий");
        }
    }

    @Transactional
    @Override
    public List<CategoryDTO> importCategoriesFromJson(byte[] data) {
        try {
            List<CategoryDTO> dtos = objectMapper.readValue(data, new TypeReference<>() {});
            List<Category> categories = categoryMapper.toEntityList(dtos);
            categories.forEach(categoryDao::create);
            log.info("Импортировано категорий: {}", categories.size());
            return dtos;
        } catch (IOException e) {
            log.error("Ошибка десериализации категорий", e);
            throw new DataImportException("Ошибка импорта категорий");
        }
    }
}