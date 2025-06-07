package com.example.service.impl;

import com.example.dao.ProductDao;
import com.example.dto.ProductDTO;
import com.example.entity.Product;
import com.example.exceptions.DataExportException;
import com.example.exceptions.DataImportException;
import com.example.exceptions.ProductNotFoundException;
import com.example.mapper.ProductMapper;
import com.example.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;
    private final ProductMapper productMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public Product createProduct(Product product) {
        Product createdProduct = productDao.create(product);
        log.info("Создан новый продукт: ID={}, название='{}', категория ID={}",
                createdProduct.getProductId(),
                createdProduct.getProductName(),
                createdProduct.getCategory().getCategoryId());
        return createdProduct;
    }

    @Transactional(readOnly = true)
    @Override
    public Product getProductById(Long id) {
        Product product = productDao.findById(id);
        if (product == null) {
            throw new ProductNotFoundException(id);
        }
        log.info("Получен продукт: ID={}, название='{}'",
                id,
                product.getProductName());
        return product;
    }

    @Transactional
    @Override
    public void updateProduct(Product product) {
        product.setUpdatedAt(LocalDateTime.now());
        if(productDao.findById(product.getProductId()) == null) {
            throw new ProductNotFoundException(product.getProductId());
        }
        productDao.update(product);
        log.info("Обновлен продукт: ID={}, новое название='{}'",
                product.getProductId(),
                product.getProductName());
    }

    @Transactional
    @Override
    public void deleteProduct(Long id) {
        if (productDao.findById(id) == null) {
            throw new ProductNotFoundException(id);
        }
        productDao.delete(id);
        log.info("Удален продукт ID={}", id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Product> getAllProducts() {
        List<Product> products = productDao.findAll();
        log.info("Получено {} продуктов",
                products.size());
        return products;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        List<Product> products = productDao.findByCategoryId(categoryId);
        if (products.isEmpty()) {
            log.debug("Не найдены продукты для категории ID={}", categoryId);
        }
        return products;
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] exportProductsToJson() {
        try {
            List<ProductDTO> productDTOs = productMapper.toDtoList(productDao.findAll());
            log.info("Экспортировано {} продуктов в JSON", productDTOs.size());
            return objectMapper.writeValueAsBytes(productDTOs);
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации категорий", e);
            throw new DataExportException("Ошибка экспорта категорий");
        }
    }

    @Transactional
    @Override
    public List<ProductDTO> importProductsFromJson(byte[] data) {
        try {
            List<ProductDTO> productDTOS = objectMapper.readValue(data, new TypeReference<>() {
            });
            List<Product> products = productMapper.toEntityList(productDTOS);
            products.forEach(productDao::create);
            log.info("Импортировано {} продуктов из JSON", products.size());
            return productDTOS;
        } catch (IOException e) {
            log.error("Ошибка десериализации категорий", e);
            throw new DataImportException("Ошибка импорта категорий");
        }
    }
}