package com.example.dao;

import com.example.entity.Price;

import java.util.List;

// Стоит ли делать так?
// public interface PriceDao extends GenericDao<Price, Long> {
// Вроде, считается хорошей практикой
public interface PriceDao extends GenericDao<Price> {

    List<Price> findByProductId(Long productId);
}
