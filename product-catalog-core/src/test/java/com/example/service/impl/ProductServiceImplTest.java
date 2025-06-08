package com.example.service.impl;

import com.example.dto.ProductDTO;
import com.example.entity.Category;
import com.example.entity.Product;
import com.example.mapper.ProductMapper;
import com.example.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        productService = new ProductServiceImpl(productRepository, productMapper, objectMapper);
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
        when(productRepository.save(product)).thenReturn(product);

        // WHEN
        Product result = productService.createProduct(product);

        // THEN
        assertEquals(product, result);
        verify(productRepository).save(product);
    }

    @Test
    void testGetProductById() {
        // GIVEN
        Long id = 1L;
        Product product = new Product();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        // WHEN
        Product result = productService.getProductById(id);

        // THEN
        assertEquals(product, result);
        verify(productRepository).findById(id);
    }

    @Test
    void testUpdateProduct() {
        // GIVEN
        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("Test Product");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // WHEN
        productService.updateProduct(product);

        // THEN
        verify(productRepository).save(product);
        assertNotNull(product.getUpdatedAt());
    }

    @Test
    void testDeleteProduct() {
        // GIVEN
        Long id = 1L;
        when(productRepository.existsById(id)).thenReturn(true);

        // WHEN
        productService.deleteProduct(id);

        // THEN
        verify(productRepository).deleteById(id);
    }

    @Test
    void testGetAllProducts() {
        // GIVEN
        Product product1 = new Product();
        Product product2 = new Product();
        when(productRepository.findAllWithCategory()).thenReturn(List.of(product1, product2));

        // WHEN
        List<Product> result = productService.getAllProducts();

        // THEN
        assertEquals(2, result.size());
        verify(productRepository).findAllWithCategory();
    }

    @Test
    void testGetProductsByCategoryId() {
        // GIVEN
        Long categoryId = 1L;
        Product product = new Product();
        when(productRepository.findByCategory_CategoryId(categoryId)).thenReturn(List.of(product));

        // WHEN
        List<Product> result = productService.getProductsByCategoryId(categoryId);

        // THEN
        assertEquals(1, result.size());
        verify(productRepository).findByCategory_CategoryId(categoryId);
    }

    @Test
    void testExportProductsToJson() {
        // GIVEN
        Product product = new Product();
        when(productRepository.findAllWithCategory()).thenReturn(List.of(product));

        // WHEN
        byte[] result = productService.exportProductsToJson();

        // THEN
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testImportProductsFromJson() {
        // GIVEN
        String jsonData = "[{\"productId\":1,\"productName\":\"Test Product\",\"categoryId\":1}]";
        byte[] data = jsonData.getBytes();
        Product product = new Product();

        Category category = new Category("Test", null);
        product.setProductId(1L);
        product.setProductName("Test Product");
        product.setCategory(category);

        when(productMapper.toEntityList(anyList())).thenReturn(List.of(product));
        when(productRepository.saveAll(anyList())).thenReturn(List.of(product));

        // WHEN
        List<ProductDTO> result = productService.importProductsFromJson(data);

        // THEN
        assertEquals(1, result.size());
        assertEquals(product.getProductId(), result.get(0).getProductId());
        verify(productRepository).saveAll(anyList());
    }
}
