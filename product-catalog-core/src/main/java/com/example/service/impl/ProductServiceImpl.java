package com.example.service.impl;

import com.example.dao.ProductDao;
import com.example.dto.ProductDTO;
import com.example.entity.Product;
import com.example.mapper.ProductMapper;
import com.example.service.ProductService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;
    private final ProductMapper productMapper;
    private final ObjectMapper objectMapper;

    public ProductServiceImpl(ProductDao productDao, ProductMapper productMapper,
                              ObjectMapper objectMapper) {
        this.productDao = productDao;
        this.productMapper = productMapper;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @Override
    public Product createProduct(Product product) {
        try {
            Product createdProduct = productDao.create(product);
            log.info("Создан новый продукт: ID={}, название='{}', категория ID={}",
                    createdProduct.getProductId(),
                    createdProduct.getProductName(),
                    createdProduct.getCategory().getCategoryId());
            return createdProduct;
        } catch (Exception e) {
            log.error("Ошибка создания продукта. Название: '{}'. Ошибка: {}",
                    product.getProductName(),
                    e.getMessage());
            throw new RuntimeException("Ошибка при создании продукта", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Product getProductById(Long id) {
        try {
            Product product = productDao.findById(id);
            if (product == null) {
                log.info("Продукт с ID={} не найден", id);
                throw new RuntimeException("Продукт не найден");
            }
            log.info("Получен продукт: ID={}, название='{}'",
                    id,
                    product.getProductName());
            return product;
        } catch (Exception e) {
            log.error("Ошибка получения продукта ID={}. Ошибка: {}",
                    id,
                    e.getMessage());
            throw new RuntimeException("Ошибка при получении продукта", e);
        }
    }

    @Transactional
    @Override
    public void updateProduct(Product product) {
        try {
            product.setUpdatedAt(LocalDateTime.now());
            productDao.update(product);
            log.info("Обновлен продукт: ID={}, новое название='{}'",
                    product.getProductId(),
                    product.getProductName());
        } catch (Exception e) {
            log.error("Ошибка обновления продукта ID={}. Ошибка: {}",
                    product.getProductId(),
                    e.getMessage());
            throw new RuntimeException("Ошибка при обновлении продукта", e);
        }
    }

    @Transactional
    @Override
    public void deleteProduct(Long id) {
        try {
            productDao.delete(id);
            log.info("Удален продукт ID={}", id);
        } catch (Exception e) {
            log.error("Ошибка удаления продукта ID={}. Ошибка: {}",
                    id,
                    e.getMessage());
            throw new RuntimeException("Ошибка при удалении продукта", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Product> getAllProducts() {
        try {
            List<Product> products = productDao.findAll();
            log.info("Получен список продуктов. Найдено {} элементов",
                    products.size());
            return products;
        } catch (Exception e) {
            log.error("Ошибка получения списка продуктов. Ошибка: {}",
                    e.getMessage());
            throw new RuntimeException("Ошибка при получении списка продуктов", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        try {
            List<Product> products = productDao.findByCategoryId(categoryId);
            log.info("Получены продукты категории ID={}. Найдено {} элементов",
                    categoryId,
                    products.size());
            return products;
        } catch (Exception e) {
            log.error("Ошибка получения продуктов категории ID={}. Ошибка: {}",
                    categoryId,
                    e.getMessage());
            throw new RuntimeException("Ошибка при получении продуктов по категории", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] exportProductsToJson() {
        try {
            List<Product> products = productDao.findAll();
            List<ProductDTO> productDTOs = productMapper.toDtoList(products);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            objectMapper.writeValue(outputStream, productDTOs);

            log.info("Экспортировано {} продуктов в JSON", products.size());
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Ошибка экспорта продуктов. Ошибка: {}", e.getMessage());
            throw new RuntimeException("Ошибка при экспорте продуктов", e);
        }
    }

    @Transactional
    @Override
    public List<ProductDTO> importProductsFromJson(byte[] data) {
        try {
            List<ProductDTO> productDTOS = objectMapper.readValue(
                    data,
                    new TypeReference<>() {}
            );

            List<Product> products = productMapper.toEntityList(productDTOS);
            products.forEach(productDao::create);

            log.info("Импортировано {} продуктов из JSON", products.size());
            return productDTOS;
        } catch (Exception e) {
            log.error("Ошибка импорта продуктов. Ошибка: {}", e.getMessage());
            throw new RuntimeException("Ошибка при импорте продуктов", e);
        }
    }
}