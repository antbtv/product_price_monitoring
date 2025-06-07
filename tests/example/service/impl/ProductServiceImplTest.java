package com.example.service.impl;

import com.example.repository.ProductDao;
import com.example.dto.ProductDTO;
import com.example.entity.Category;
import com.example.entity.Product;
import com.example.mapper.ProductMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductDao productDao;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        productService = new ProductServiceImpl(productDao, productMapper, objectMapper);
    }

    @Test
    void testCreateProduct() {
        // GIVEN
        Product product = new Product();
        Category category = new Category();
        category.setCategoryName("Test");
        category.setCategoryId(1L);
        product.setProductName("Test");
        product.setCategory(category);
        Mockito.when(productDao.create(product)).thenReturn(product);

        // WHEN
        Product result = productService.createProduct(product);

        // THEN
        Assertions.assertEquals(product, result);
        Mockito.verify(productDao).create(product);
    }

    @Test
    void testGetProductById() {
        // GIVEN
        Long id = 1L;
        Product product = new Product();
        Mockito.when(productDao.findById(id)).thenReturn(product);

        // WHEN
        Product result = productService.getProductById(id);

        // THEN
        Assertions.assertEquals(product, result);
        Mockito.verify(productDao).findById(id);
    }

    @Test
    void testUpdateProduct() {
        // GIVEN
        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("Test Product");
        Mockito.when(productDao.findById(1L)).thenReturn(product);

        // WHEN
        productService.updateProduct(product);

        // THEN
        Mockito.verify(productDao).update(product);
        Assertions.assertNotNull(product.getUpdatedAt());
    }

    @Test
    void testDeleteProduct() {
        // GIVEN
        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("Test Product");
        Mockito.when(productDao.findById(1L)).thenReturn(product);

        // WHEN
        productService.deleteProduct(1L);

        // THEN
        Mockito.verify(productDao).delete(1L);
    }

    @Test
    void testGetAllProducts() {
        // GIVEN
        Product product1 = new Product();
        Product product2 = new Product();
        Mockito.when(productDao.findAll()).thenReturn(List.of(product1, product2));

        // WHEN
        List<Product> result = productService.getAllProducts();

        // THEN
        Assertions.assertEquals(2, result.size());
        Mockito.verify(productDao).findAll();
    }

    @Test
    void testGetProductsByCategoryId() {
        // GIVEN
        Long categoryId = 1L;
        Product product = new Product();
        Mockito.when(productDao.findByCategoryId(categoryId)).thenReturn(List.of(product));

        // WHEN
        List<Product> result = productService.getProductsByCategoryId(categoryId);

        // THEN
        Assertions.assertEquals(1, result.size());
        Mockito.verify(productDao).findByCategoryId(categoryId);
    }

    @Test
    void testExportProductsToJson() throws IOException {
        // GIVEN
        Product product = new Product();
        Mockito.when(productDao.findAll()).thenReturn(List.of(product));

        // WHEN
        byte[] result = productService.exportProductsToJson();

        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.length > 0);
    }

    @Test
    void testImportProductsFromJson() throws IOException {
        // GIVEN
        String jsonData = "[{\"productId\":1,\"productName\":\"Test Product\",\"categoryId\":1}]";
        byte[] data = jsonData.getBytes();
        Product product = new Product();

        Category category = new Category("Test", null);
        product.setProductId(1L);
        product.setProductName("Test Product");
        product.setCategory(category);

        Mockito.when(productMapper.toEntityList(ArgumentMatchers.anyList())).thenReturn(List.of(product));
        Mockito.when(productDao.create(ArgumentMatchers.any(Product.class))).thenReturn(new Product());

        // WHEN
        List<ProductDTO> result = productService.importProductsFromJson(data);

        // THEN
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(product.getProductId(), result.get(0).getProductId());
        Mockito.verify(productDao).create(ArgumentMatchers.any(Product.class));
    }
}
