package com.example.service;

import com.example.dto.ProductDTO;
import com.example.dto.StoreDTO;
import com.example.entity.Product;

import java.io.IOException;
import java.util.List;

/**
 * Интерфейс для управления товарами в системе
 */
public interface ProductService {

    /**
     * Создает новый товар
     *
     * @param product сущность товара для создания
     * @return созданная сущность товара
     */
    Product createProduct(Product product);

    /**
     * Получает товар по id
     *
     * @param id id товара
     * @return найденная сущность товара
     */
    Product getProductById(Long id);

    /**
     * Обновляет существующий товар
     *
     * @param product сущность товара с обновленными данными
     */
    void updateProduct(Product product);

    /**
     * Удаляет товар по id
     *
     * @param id id товара для удаления
     */
    void deleteProduct(Long id);

    /**
     * Получает список всех товаров
     *
     * @return список сущностей товаров
     */
    List<Product> getAllProducts();

    /**
     * Получает товары по id категории
     * 
     * @param categoryId id категории
     * @return список товаров принадлежащих указанной категории
     */
    List<Product> getProductsByCategoryId(Long categoryId);

    /**
     * Экспортирует товары в JSON-формате
     * 
     * @return массив байтов с данными в JSON-формате
     * @throws IOException при ошибках ввода-вывода
     */
    byte[] exportProductsToJson() throws IOException;

    /**
     * Импортирует товары из JSON-данных
     * 
     * @param data массив байтов с JSON-данными
     * @return список импортированных DTO товаров
     * @throws IOException при ошибках парсинга или ввода-вывода
     */
    List<ProductDTO> importProductsFromJson(byte[] data) throws IOException;
}