package com.example.controller;

import com.example.dto.ProductCreateDTO;
import com.example.dto.ProductDTO;
import com.example.entity.Category;
import com.example.entity.Product;
import com.example.mapper.ProductMapper;
import com.example.service.CategoryService;
import com.example.service.DataLogService;
import com.example.service.ProductService;
import com.example.service.security.UserService;
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
    private UserService userService;

    @Mock
    private DataLogService dataLogService;

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
        ProductCreateDTO createDTO = new ProductCreateDTO("Milk", 1L);
        Category category = new Category("Bread", null);
        category.setCategoryId(1L);

        Product product = new Product("Milk", category);
        product.setProductId(1L);
        product.setCreatedAt(testTime);
        product.setUpdatedAt(testTime);

        ProductDTO productDTO = new ProductDTO(1L, "Milk", 1L, testTime, testTime);

        Mockito.when(categoryService.getCategoryById(1L)).thenReturn(category);
        Mockito.when(productService.createProduct(ArgumentMatchers.any(Product.class))).thenReturn(product);
        Mockito.when(productMapper.toDto(product)).thenReturn(productDTO);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").value("Milk"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").exists());

        // THEN
        Mockito.verify(categoryService).getCategoryById(1L);
        Mockito.verify(productService).createProduct(ArgumentMatchers.any(Product.class));
        Mockito.verify(productMapper).toDto(product);
    }

    @Test
    void testGetProductById() throws Exception {
        // GIVEN
        Product product = new Product("Water", new Category());
        product.setProductId(1L);
        product.setCreatedAt(testTime);
        product.setUpdatedAt(testTime);

        ProductDTO productDTO = new ProductDTO(1L, "Water", 1L, testTime, testTime);

        Mockito.when(productService.getProductById(1L)).thenReturn(product);
        Mockito.when(productMapper.toDto(product)).thenReturn(productDTO);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/products/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").value("Water"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").exists());

        // THEN
        Mockito.verify(productService).getProductById(1L);
        Mockito.verify(productMapper).toDto(product);
    }

    @Test
    void testGetProductById_NotFound() throws Exception {
        // GIVEN
        Mockito.when(productService.getProductById(1L)).thenReturn(null);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/products/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        // THEN
        Mockito.verify(productService).getProductById(1L);
        Mockito.verifyNoInteractions(productMapper);
    }

    @Test
    void testUpdateProduct() throws Exception {
        // GIVEN
        Long productId = 1L;
        ProductDTO requestDTO = new ProductDTO();
        requestDTO.setProductName("Updated Product");
        requestDTO.setCategoryId(2L);

        Product updatedProduct = new Product("Updated Product", new Category());
        updatedProduct.setProductId(productId);

        Mockito.when(productMapper.toEntity(ArgumentMatchers.any(ProductDTO.class))).thenReturn(updatedProduct);
        Mockito.doNothing().when(productService).updateProduct(ArgumentMatchers.any(Product.class));

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").value("Updated Product"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId").value(2L));

        // THEN
        Mockito.verify(productMapper).toEntity(ArgumentMatchers.argThat(dto ->
                dto.getProductName().equals("Updated Product") &&
                        dto.getCategoryId() == 2L
        ));
        Mockito.verify(productService).updateProduct(ArgumentMatchers.any(Product.class));
    }

    @Test
    void testPartialUpdateProduct() throws Exception {
        // GIVEN
        ProductCreateDTO updateDTO = new ProductCreateDTO("New Name", null);
        Product existingProduct = new Product("Old Name", new Category());
        existingProduct.setProductId(1L);
        existingProduct.setCreatedAt(testTime);

        ProductDTO productDTO = new ProductDTO(1L, "New Name", 1L, testTime, testTime.plusHours(1));

        Mockito.when(productService.getProductById(1L)).thenReturn(existingProduct);
        Mockito.doNothing().when(productService).updateProduct(ArgumentMatchers.any(Product.class));
        Mockito.when(productMapper.toDto(existingProduct)).thenReturn(productDTO);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.patch("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").value("New Name"));

        // THEN
        Mockito.verify(productService).getProductById(1L);
        Mockito.verify(productService).updateProduct(ArgumentMatchers.any(Product.class));
        Mockito.verify(productMapper).toDto(existingProduct);
    }

    @Test
    void testDeleteProduct() throws Exception {
        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // THEN
        Mockito.verify(productService).deleteProduct(1L);
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

        Mockito.when(productService.getAllProducts()).thenReturn(products);
        Mockito.when(productMapper.toDtoList(products)).thenReturn(productDTOs);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].productName").value("Product 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].productName").value("Product 2"));

        // THEN
        Mockito.verify(productService).getAllProducts();
        Mockito.verify(productMapper).toDtoList(products);
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

        Mockito.when(productService.getProductsByCategoryId(1L)).thenReturn(products);
        Mockito.when(productMapper.toDtoList(products)).thenReturn(productDTOs);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/products/category/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].productName").value("Product 1"));

        // THEN
        Mockito.verify(productService).getProductsByCategoryId(1L);
        Mockito.verify(productMapper).toDtoList(products);
    }

    @Test
    void testExportProducts() throws Exception {
        // GIVEN
        byte[] mockData = "{\"products\":[]}".getBytes();
        Mockito.when(productService.exportProductsToJson()).thenReturn(mockData);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/products/export"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"products.json\""))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().bytes(mockData));

        // THEN
        Mockito.verify(productService).exportProductsToJson();
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

        Mockito.when(productService.importProductsFromJson(jsonData))
                .thenReturn(List.of(importedProduct));

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.multipart("/products/import")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].productName").value("Imported"));

        // THEN
        Mockito.verify(productService).importProductsFromJson(jsonData);
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
        mockMvc.perform(MockMvcRequestBuilders.multipart("/products/import")
                        .file(emptyFile))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // THEN
        Mockito.verifyNoInteractions(productService);
    }
}