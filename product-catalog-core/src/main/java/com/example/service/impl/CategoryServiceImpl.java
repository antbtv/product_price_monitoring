package com.example.service.impl;

import com.example.dao.CategoryDao;
import com.example.dto.CategoryDTO;
import com.example.entity.Category;
import com.example.mapper.CategoryMapper;
import com.example.service.CategoryService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
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
        try {
            Category createdCategory = categoryDao.create(category);
            log.info("Успешное создание категории. ID: {}, название: {}",
                    createdCategory.getCategoryId(),
                    createdCategory.getCategoryName());
            return createdCategory;
        } catch (Exception e) {
            log.error("Ошибка создания категории. Название: {}. Ошибка: {}",
                    category.getCategoryName(),
                    e.getMessage());
            throw new RuntimeException("Ошибка при создании категории", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Category getCategoryById(Long id) {
        try {
            Category category = categoryDao.findById(id);
            if (category == null) {
                log.info("Категория с ID {} не найдена", id);
                throw new RuntimeException("Категория не найдена");
            }
            log.info("Успешное получение категории. ID: {}, название: {}",
                    id,
                    category.getCategoryName());
            return category;
        } catch (Exception e) {
            log.error("Ошибка получения категории. ID: {}. Ошибка: {}",
                    id,
                    e.getMessage());
            throw new RuntimeException("Ошибка при получении категории", e);
        }
    }

    @Transactional
    @Override
    public void updateCategory(Category category) {
        try {
            categoryDao.update(category);
            log.info("Успешное обновление категории. ID: {}, новое название: {}",
                    category.getCategoryId(),
                    category.getCategoryName());
        } catch (Exception e) {
            log.error("Ошибка обновления категории. ID: {}. Ошибка: {}",
                    category.getCategoryId(),
                    e.getMessage());
            throw new RuntimeException("Ошибка при обновлении категории", e);
        }
    }

    @Transactional
    @Override
    public void deleteCategory(Long id) {
        try {
            categoryDao.delete(id);
            log.info("Успешное удаление категории. ID: {}", id);
        } catch (Exception e) {
            log.error("Ошибка удаления категории. ID: {}. Ошибка: {}",
                    id,
                    e.getMessage());
            throw new RuntimeException("Ошибка при удалении категории", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Category> getAllCategories() {
        try {
            List<Category> categories = categoryDao.findAll();
            log.info("Успешное получение списка категорий. Найдено {} категорий",
                    categories.size());
            return categories;
        } catch (Exception e) {
            log.error("Ошибка получения списка категорий. Ошибка: {}",
                    e.getMessage());
            throw new RuntimeException("Ошибка при получении списка категорий", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] exportCategoriesToJson() {
        try {
            List<Category> categories = categoryDao.findAll();
            List<CategoryDTO> categoryDTOS = categoryMapper.toDtoList(categories);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            objectMapper.writeValue(outputStream, categoryDTOS);

            log.info("Успешный экспорт категорий. Экспортировано {} категорий",
                    categories.size());
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Ошибка экспорта категорий. Ошибка: {}",
                    e.getMessage());
            throw new RuntimeException("Ошибка при экспорте категорий", e);
        }
    }

    @Transactional
    @Override
    public List<CategoryDTO> importCategoriesFromJson(byte[] data) {
        try {
            List<CategoryDTO> categoryDTOS = objectMapper.readValue(
                    data,
                    new TypeReference<>() {}
            );

            List<Category> categories = categoryMapper.toEntityList(categoryDTOS);
            categories.forEach(categoryDao::create);

            log.info("Успешный импорт категорий. Импортировано {} категорий",
                    categories.size());
            return categoryDTOS;
        } catch (Exception e) {
            log.error("Ошибка импорта категорий. Ошибка: {}",
                    e.getMessage());
            throw new RuntimeException("Ошибка при импорте категорий", e);
        }
    }
}