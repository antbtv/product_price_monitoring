package com.example.dao;

import com.example.entity.Product;

import java.util.List;

public interface ProductDao extends GenericDao<Product> {
    /**
     * Получение списка продуктов определённой категории
     *
     * @param categoryId id категории
     * @return список продуктов
     */
    List<Product> findByCategoryId(Long categoryId);
}