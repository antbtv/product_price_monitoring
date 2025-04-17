package com.example.service.impl;

import com.example.dao.ProductDao;
import com.example.entity.Product;
import com.example.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Transactional
    @Override
    public void createProduct(Product product) {
        productDao.create(product);
    }

    @Transactional(readOnly = true)
    @Override
    public Product getProductById(int id) {
        return productDao.findById(id);
    }

    @Transactional
    @Override
    public void updateProduct(Product product) {
        productDao.update(product);
    }

    @Transactional
    @Override
    public void deleteProduct(int id) {
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
}
