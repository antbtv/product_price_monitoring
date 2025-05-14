package com.example.controller;

import com.example.dto.ProductCreateDTO;
import com.example.dto.ProductDTO;
import com.example.entity.Category;
import com.example.entity.Product;
import com.example.mapper.ProductMapper;
import com.example.service.CategoryService;
import com.example.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private final LocalDateTime testTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCreateProduct() throws Exception {
        // GIVEN
        ProductCreateDTO createDTO = new ProductCreateDTO("Smartphone", 1L);
        Category category = new Category("Electronics", null);
        category.setCategoryId(1L);

        Product product = new Product("Smartphone", category);
        product.setProductId(1L);
        product.setCreatedAt(testTime);
        product.setUpdatedAt(testTime);

        ProductDTO productDTO = new ProductDTO(1L, "Smartphone", 1L, testTime, testTime);

        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(productService.createProduct(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDTO);

        // WHEN
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(1L))
                .andExpect(jsonPath("$.productName").value("Smartphone"))
                .andExpect(jsonPath("$.categoryId").value(1L))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        // THEN
        verify(categoryService).getCategoryById(1L);
        verify(productService).createProduct(any(Product.class));
        verify(productMapper).toDto(product);
    }

    @Test
    void testGetProductById() throws Exception {
        // GIVEN
        Product product = new Product("Laptop", new Category());
        product.setProductId(1L);
        product.setCreatedAt(testTime);
        product.setUpdatedAt(testTime);

        ProductDTO productDTO = new ProductDTO(1L, "Laptop", 1L, testTime, testTime);

        when(productService.getProductById(1L)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDTO);

        // WHEN
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1L))
                .andExpect(jsonPath("$.productName").value("Laptop"))
                .andExpect(jsonPath("$.createdAt").exists());

        // THEN
        verify(productService).getProductById(1L);
        verify(productMapper).toDto(product);
    }

    @Test
    void testGetProductById_NotFound() throws Exception {
        // GIVEN
        when(productService.getProductById(1L)).thenReturn(null);

        // WHEN
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isNotFound());

        // THEN
        verify(productService).getProductById(1L);
        verifyNoInteractions(productMapper);
    }

    @Test
    void testUpdateProduct() throws Exception {
        // GIVEN
        Product product = new Product("Updated Product", new Category());
        product.setProductId(1L);
        product.setCreatedAt(testTime);
        product.setUpdatedAt(testTime.plusHours(1));

        ProductDTO productDTO = new ProductDTO(1L, "Updated Product", 1L, testTime, testTime.plusHours(1));

        doNothing().when(productService).updateProduct(any(Product.class));
        when(productMapper.toDto(product)).thenReturn(productDTO);

        // WHEN
        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1L))
                .andExpect(jsonPath("$.productName").value("Updated Product"))
                .andExpect(jsonPath("$.updatedAt").exists());

        // THEN
        verify(productService).updateProduct(any(Product.class));
        verify(productMapper).toDto(product);
    }

    @Test
    void testPartialUpdateProduct() throws Exception {
        // GIVEN
        ProductCreateDTO updateDTO = new ProductCreateDTO("New Name", null);
        Product existingProduct = new Product("Old Name", new Category());
        existingProduct.setProductId(1L);
        existingProduct.setCreatedAt(testTime);

        ProductDTO productDTO = new ProductDTO(1L, "New Name", 1L, testTime, testTime.plusHours(1));

        when(productService.getProductById(1L)).thenReturn(existingProduct);
        doNothing().when(productService).updateProduct(any(Product.class));
        when(productMapper.toDto(existingProduct)).thenReturn(productDTO);

        // WHEN
        mockMvc.perform(patch("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("New Name"));

        // THEN
        verify(productService).getProductById(1L);
        verify(productService).updateProduct(any(Product.class));
        verify(productMapper).toDto(existingProduct);
    }

    @Test
    void testDeleteProduct() throws Exception {
        // WHEN
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());

        // THEN
        verify(productService).deleteProduct(1L);
    }

    @Test
    void testGetAllProducts() throws Exception {
        // GIVEN
        Product product1 = new Product("Product 1", new Category());
        product1.setProductId(1L);
        Product product2 = new Product("Product 2", new Category());
        product2.setProductId(2L);

        List<Product> products = List.of(product1, product2);
        List<ProductDTO> productDTOs = List.of(
                new ProductDTO(1L, "Product 1", 1L, testTime, testTime),
                new ProductDTO(2L, "Product 2", 1L, testTime, testTime)
        );

        when(productService.getAllProducts()).thenReturn(products);
        when(productMapper.toDtoList(products)).thenReturn(productDTOs);

        // WHEN
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].productName").value("Product 1"))
                .andExpect(jsonPath("$[1].productName").value("Product 2"));

        // THEN
        verify(productService).getAllProducts();
        verify(productMapper).toDtoList(products);
    }

    @Test
    void testGetProductsByCategoryId() throws Exception {
        // GIVEN
        Product product1 = new Product("Product 1", new Category());
        product1.setProductId(1L);

        List<Product> products = List.of(product1);
        List<ProductDTO> productDTOs = List.of(
                new ProductDTO(1L, "Product 1", 1L, testTime, testTime)
        );

        when(productService.getProductsByCategoryId(1L)).thenReturn(products);
        when(productMapper.toDtoList(products)).thenReturn(productDTOs);

        // WHEN
        mockMvc.perform(get("/products/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].productName").value("Product 1"));

        // THEN
        verify(productService).getProductsByCategoryId(1L);
        verify(productMapper).toDtoList(products);
    }

    @Test
    void testExportProducts() throws Exception {
        // GIVEN
        byte[] mockData = "{\"products\":[]}".getBytes();
        when(productService.exportProductsToJson()).thenReturn(mockData);

        // WHEN
        mockMvc.perform(get("/products/export"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"products.json\""))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().bytes(mockData));

        // THEN
        verify(productService).exportProductsToJson();
    }

    @Test
    void testImportProducts() throws Exception {
        // GIVEN
        byte[] jsonData = "[{\"productName\":\"Imported\"}]".getBytes();
        ProductDTO importedProduct = new ProductDTO(1L, "Imported", 1L, testTime, testTime);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "products.json",
                MediaType.APPLICATION_JSON_VALUE,
                jsonData);

        when(productService.importProductsFromJson(jsonData))
                .thenReturn(List.of(importedProduct));

        // WHEN
        mockMvc.perform(multipart("/products/import")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("Imported"));

        // THEN
        verify(productService).importProductsFromJson(jsonData);
    }

    @Test
    void testImportProducts_EmptyFile() throws Exception {
        // GIVEN
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.json",
                MediaType.APPLICATION_JSON_VALUE,
                new byte[0]);

        // WHEN
        mockMvc.perform(multipart("/products/import")
                        .file(emptyFile))
                .andExpect(status().isBadRequest());

        // THEN
        verifyNoInteractions(productService);
    }
}