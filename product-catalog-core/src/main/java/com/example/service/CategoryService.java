package com.example.service;

import com.example.dto.CategoryDTO;
import com.example.entity.Category;

import java.io.IOException;
import java.util.List;

/**
 * Интерфейс для управления категориями
 */
public interface CategoryService {

    /**
     * Создает новую категорию
     *
     * @param category сущность категории
     * @return созданная сущность категории
     */
    Category createCategory(Category category);

    /**
     * Получает категорию по id
     *
     * @param id id категории
     * @return найденная сущность категории
     */
    Category getCategoryById(Long id);

    /**
     * Обновляет существующую категорию
     *
     * @param category сущность категории
     */
    void updateCategory(Category category);

    /**
     * Удаляет категорию по id
     *
     * @param id id категории для удаления
     */
    void deleteCategory(Long id);

    /**
     * Получает список всех категорий
     *
     * @return список сущностей категорий
     */
    List<Category> getAllCategories();

    /**
     * Экспортирует категории в JSON-формате
     *
     * @return массив байтов с данными в JSON-формате
     * @throws IOException при ошибках ввода-вывода
     */
    byte[] exportCategoriesToJson() throws IOException;

    /**
     * Импортирует категории из JSON-данных
     *
     * @param data массив байтов с JSON-данными
     * @return список импортированных категорий
     * @throws IOException при ошибках парсинга или ввода-вывода
     */
    List<CategoryDTO> importCategoriesFromJson(byte[] data) throws IOException;

    /**
     * Получение всех дочерних категорий по родительскому id
     *
     * @param parentId родительский id
     * @return список дочерних категорий
     */
    List<Category> getAllCategoriesByParentId(Long parentId);
}