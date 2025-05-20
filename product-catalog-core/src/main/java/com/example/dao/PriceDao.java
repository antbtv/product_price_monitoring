package com.example.dao;

import com.example.entity.Price;

import java.util.List;

public interface PriceDao extends GenericDao<Price> {

    List<Price> findByProductId(Long productId);
}
