package com.example.service;

import com.example.entity.Product;

import java.util.List;

public interface ProductService {

    void createProduct(Product product);

    Product getProductById(Long id);

    void updateProduct(Product product);

    void deleteProduct(Long id);

    List<Product> getAllProducts();

    List<Product> getProductsByCategoryId(Long categoryId);
}
