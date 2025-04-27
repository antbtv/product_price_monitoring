package com.example.service.impl;

import com.example.dao.ProductDao;
import com.example.dto.ProductDTO;
import com.example.entity.Product;
import com.example.mapper.ProductMapper;
import com.example.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Transactional
    @Override
    public Product createProduct(Product product) {
        return productDao.create(product);
    }

    @Transactional(readOnly = true)
    @Override
    public Product getProductById(Long id) {
        return productDao.findById(id);
    }

    @Transactional
    @Override
    public void updateProduct(Product product) {
        product.setUpdatedAt(LocalDateTime.now());
        productDao.update(product);
    }

    @Transactional
    @Override
    public void deleteProduct(Long id) {
        productDao.delete(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Product> getAllProducts() {
        return productDao.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productDao.findByCategoryId(categoryId);
    }

    @Transactional(readOnly = true)
    public byte[] exportProductsToJson() throws IOException {
        List<Product> products = productDao.findAll();
        List<ProductDTO> productDTOs = ProductMapper.INSTANCE.toDtoList(products);

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        objectMapper.writeValue(outputStream, productDTOs);
        return outputStream.toByteArray();
    }
}
