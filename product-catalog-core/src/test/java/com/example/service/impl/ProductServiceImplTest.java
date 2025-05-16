package com.example.service.impl;

import com.example.dao.ProductDao;
import com.example.dto.ProductDTO;
import com.example.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void testCreateProduct() {
        // GIVEN
        Product product = new Product();
        when(productDao.create(product)).thenReturn(product);

        // WHEN
        Product result = productService.createProduct(product);

        // THEN
        assertEquals(product, result);
        verify(productDao).create(product);
    }

    @Test
    void testGetProductById() {
        // GIVEN
        Long id = 1L;
        Product product = new Product();
        when(productDao.findById(id)).thenReturn(product);

        // WHEN
        Product result = productService.getProductById(id);

        // THEN
        assertEquals(product, result);
        verify(productDao).findById(id);
    }

    @Test
    void testUpdateProduct() {
        // GIVEN
        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("Test Product");

        // WHEN
        productService.updateProduct(product);

        // THEN
        verify(productDao).update(product);
        assertNotNull(product.getUpdatedAt());
    }

    @Test
    void testDeleteProduct() {
        // GIVEN
        Long id = 1L;

        // WHEN
        productService.deleteProduct(id);

        // THEN
        verify(productDao).delete(id);
    }

    @Test
    void testGetAllProducts() {
        // GIVEN
        Product product1 = new Product();
        Product product2 = new Product();
        when(productDao.findAll()).thenReturn(List.of(product1, product2));

        // WHEN
        List<Product> result = productService.getAllProducts();

        // THEN
        assertEquals(2, result.size());
        verify(productDao).findAll();
    }

    @Test
    void testGetProductsByCategoryId() {
        // GIVEN
        Long categoryId = 1L;
        Product product = new Product();
        when(productDao.findByCategoryId(categoryId)).thenReturn(List.of(product));

        // WHEN
        List<Product> result = productService.getProductsByCategoryId(categoryId);

        // THEN
        assertEquals(1, result.size());
        verify(productDao).findByCategoryId(categoryId);
    }

    @Test
    void testExportProductsToJson() throws IOException {
        // GIVEN
        Product product = new Product();
        when(productDao.findAll()).thenReturn(List.of(product));

        // WHEN
        byte[] result = productService.exportProductsToJson();

        // THEN
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testImportProductsFromJson() throws IOException {
        // GIVEN
        String jsonData = "[{\"productId\":1,\"productName\":\"Test Product\",\"categoryId\":1}]";
        byte[] data = jsonData.getBytes();
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(1L);
        productDTO.setProductName("Test Product");
        productDTO.setCategoryId(1L);
        when(productDao.create(any(Product.class))).thenReturn(new Product());

        // WHEN
        List<ProductDTO> result = productService.importProductsFromJson(data);

        // THEN
        assertEquals(1, result.size());
        assertEquals(productDTO.getProductId(), result.get(0).getProductId());
        verify(productDao).create(any(Product.class));
    }
}
