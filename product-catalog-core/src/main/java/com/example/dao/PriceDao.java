package com.example.dao;

import com.example.entity.Price;

import java.util.List;

public interface PriceDao extends GenericDao<Price> {

    /**
     * Получение списка цен на определенный продукт
     *
     * @param productId id продукта
     * @return список цен
     */
    List<Price> findByProductId(Long productId);
}
