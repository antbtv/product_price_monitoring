package com.example.service.impl;

import com.example.repository.CategoryRepository;
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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public Category createCategory(Category category) {
        Category createdCategory = categoryRepository.save(category);
        log.info("Создана категория ID: {}, название: {}",
                createdCategory.getCategoryId(),
                createdCategory.getCategoryName());
        return createdCategory;
    }

    @Transactional(readOnly = true)
    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Transactional
    @Override
    public void updateCategory(Category category) {
        if (!categoryRepository.existsById(category.getCategoryId())) {
            throw new CategoryNotFoundException(category.getCategoryId());
        }
        categoryRepository.save(category);
        log.info("Обновлена категория ID: {}", category.getCategoryId());
    }

    @Transactional
    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        categoryRepository.deleteById(id);
        log.info("Удалена категория ID: {}", id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAllWithSubCategories();
        log.info("Найдено категорий: {}", categories.size());
        return categories;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Category> getAllCategoriesByParentId(Long parentId) {
        if (parentId != null && !categoryRepository.existsById(parentId)) {
            log.warn("Попытка получить дочерние категории для несуществующего родителя ID: {}", parentId);
            throw new CategoryNotFoundException(parentId);
        }
        List<Category> categories = categoryRepository.findAllByParentId(parentId);
        log.info("Найдено дочерних категорий для родителя ID {}: {}", parentId, categories.size());
        return categories;
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] exportCategoriesToJson() {
        try {
            List<CategoryDTO> dtos = categoryMapper.toDtoList(categoryRepository.findAllWithSubCategories());
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
            categoryRepository.saveAll(categories);
            log.info("Импортировано категорий: {}", categories.size());
            return dtos;
        } catch (IOException e) {
            log.error("Ошибка десериализации категорий", e);
            throw new DataImportException("Ошибка импорта категорий");
        }
    }
}