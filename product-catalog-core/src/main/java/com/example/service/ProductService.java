package com.example.service;

import com.example.dto.ProductDTO;
import com.example.dto.StoreDTO;
import com.example.entity.Product;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    Product createProduct(Product product);

    Product getProductById(Long id);

    void updateProduct(Product product);

    void deleteProduct(Long id);

    List<Product> getAllProducts();

    List<Product> getProductsByCategoryId(Long categoryId);

    byte[] exportProductsToJson() throws IOException;

    List<ProductDTO> importProductsFromJson(byte[] data) throws IOException;
}
