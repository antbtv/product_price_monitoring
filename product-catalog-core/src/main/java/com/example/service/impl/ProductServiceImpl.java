package com.example.service.impl;

import com.example.MessageSources;
import com.example.dao.ProductDao;
import com.example.dto.ProductDTO;
import com.example.dto.StoreDTO;
import com.example.entity.Product;
import com.example.mapper.ProductMapper;
import com.example.service.ProductService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;
    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Transactional
    @Override
    public Product createProduct(Product product) {
        try {
            return productDao.create(product);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_CREATE);
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Product getProductById(Long id) {
        try {
            return productDao.findById(id);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_ONE);
            return null;
        }
    }

    @Transactional
    @Override
    public void updateProduct(Product product) {
        try {
            product.setUpdatedAt(LocalDateTime.now());
            productDao.update(product);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_UPDATE);
        }
    }

    @Transactional
    @Override
    public void deleteProduct(Long id) {
        try {
            productDao.delete(id);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_DELETE);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Product> getAllProducts() {
        try {
            return productDao.findAll();
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_MANY);
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        try {
            return productDao.findByCategoryId(categoryId);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_MANY);
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    @Override
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

    @Transactional
    @Override
    public List<ProductDTO> importProductsFromJson(byte[] data) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<ProductDTO> productDTOS = objectMapper.readValue(
                data,
                new TypeReference<>() {
                }
        );

        List<Product> products = ProductMapper.INSTANCE.toEntityList(productDTOS);
        products.forEach(productDao::create);

        return productDTOS;
    }
}
