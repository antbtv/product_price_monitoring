package com.example.dao;

import com.example.entity.Product;

import java.util.List;

public interface ProductDao extends GenericDao<Product> {
    List<Product> findByCategoryId(Long categoryId);
}