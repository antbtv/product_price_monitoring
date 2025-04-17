package com.example.service;

import com.example.entity.Product;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductService {

    void createProduct(Product product);

    Product getProductById(int id);

    void updateProduct(Product product);

    void deleteProduct(int id);

    List<Product> getAllProducts();

    List<Product> getProductsByCategoryId(Long categoryId);
}
