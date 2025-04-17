package com.example.service.impl;

import com.example.dao.ProductDao;
import com.example.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl {

    private final ProductDao productDao;

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Transactional
    public void createProduct(Product product) {
        productDao.create(product);
    }

    @Transactional(readOnly = true)
    public Product getProductById(int id) {
        return productDao.findById(id);
    }

    @Transactional
    public void updateProduct(Product product) {
        productDao.update(product);
    }

    @Transactional
    public void deleteProduct(int id) {
        productDao.delete(id);
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productDao.findAll();
    }
}
