package com.example.controller;

import com.example.dto.CategoryCreateDTO;
import com.example.dto.CategoryDTO;
import com.example.entity.Category;
import com.example.mapper.CategoryMapper;
import com.example.service.CategoryService;
import com.example.service.DataLogService;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserService userService;

    @Mock
    private DataLogService dataLogService;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryController categoryController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private final LocalDateTime testTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCreateCategory() throws Exception {
        // GIVEN
        CategoryCreateDTO createDTO = new CategoryCreateDTO("Electronics", 1L);
        Category parent = new Category("Parent", null);
        parent.setCategoryId(1L);
        Category category = new Category("Electronics", parent);
        category.setCategoryId(2L);
        category.setCreatedAt(testTime);
        category.setUpdatedAt(testTime);

        CategoryDTO categoryDTO = new CategoryDTO(2L, "Electronics", 1L, testTime, testTime);

        Mockito.when(categoryService.getCategoryById(1L)).thenReturn(parent);
        Mockito.when(categoryService.createCategory(ArgumentMatchers.any(Category.class))).thenReturn(category);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(categoryDTO);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryName").value("Electronics"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.parentId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").exists());

        // THEN
        Mockito.verify(categoryService).getCategoryById(1L);
        Mockito.verify(categoryService).createCategory(ArgumentMatchers.any(Category.class));
        Mockito.verify(categoryMapper).toDto(category);
    }

    @Test
    void testGetCategoryById() throws Exception {
        // GIVEN
        Category category = new Category("Electronics", null);
        category.setCategoryId(1L);
        category.setCreatedAt(testTime);
        category.setUpdatedAt(testTime);

        CategoryDTO categoryDTO = new CategoryDTO(1L, "Electronics", null, testTime, testTime);

        Mockito.when(categoryService.getCategoryById(1L)).thenReturn(category);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(categoryDTO);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/categories/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryName").value("Electronics"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").exists());

        // THEN
        Mockito.verify(categoryService).getCategoryById(1L);
        Mockito.verify(categoryMapper).toDto(category);
    }

    @Test
    void testUpdateCategory() throws Exception {
        // GIVEN
        Long categoryId = 1L;
        CategoryDTO requestDTO = new CategoryDTO();
        requestDTO.setCategoryId(categoryId);
        requestDTO.setCategoryName("Updated Category");
        requestDTO.setCreatedAt(testTime);
        requestDTO.setUpdatedAt(testTime.plusHours(1));

        Category categoryEntity = new Category();
        categoryEntity.setCategoryId(categoryId);
        categoryEntity.setCategoryName("Updated Category");
        categoryEntity.setCreatedAt(testTime);
        categoryEntity.setUpdatedAt(testTime.plusHours(1));

        Mockito.when(categoryMapper.toEntity(ArgumentMatchers.any(CategoryDTO.class))).thenReturn(categoryEntity);
        Mockito.doNothing().when(categoryService).updateCategory(ArgumentMatchers.any(Category.class));

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.put("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId").value(categoryId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryName").value("Updated Category"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").exists());

        // THEN
        Mockito.verify(categoryMapper).toEntity(ArgumentMatchers.argThat(dto ->
                dto.getCategoryId().equals(categoryId) &&
                        dto.getCategoryName().equals("Updated Category")
        ));
        Mockito.verify(categoryService).updateCategory(ArgumentMatchers.any(Category.class));
    }


    @Test
    void testGetAllCategories() throws Exception {
        // GIVEN
        Category category1 = new Category("Electronics", null);
        category1.setCategoryId(1L);
        category1.setCreatedAt(testTime);
        category1.setUpdatedAt(testTime);

        Category category2 = new Category("Books", null);
        category2.setCategoryId(2L);
        category2.setCreatedAt(testTime);
        category2.setUpdatedAt(testTime);

        List<Category> categories = List.of(category1, category2);
        List<CategoryDTO> categoryDTOs = List.of(
                new CategoryDTO(1L, "Electronics", null, testTime, testTime),
                new CategoryDTO(2L, "Books", null, testTime, testTime)
        );

        Mockito.when(categoryService.getAllCategories()).thenReturn(categories);
        Mockito.when(categoryMapper.toDtoList(categories)).thenReturn(categoryDTOs);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/categories"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].categoryName").value("Electronics"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].categoryName").value("Books"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].updatedAt").exists());

        // THEN
        Mockito.verify(categoryService).getAllCategories();
        Mockito.verify(categoryMapper).toDtoList(categories);
    }

    @Test
    void testExportCategories() throws Exception {
        // GIVEN
        byte[] mockData = ("{\"categories\":[{\"categoryName\":\"Electronics\",\"createdAt\":\"" +
                testTime + "\"}]}").getBytes();
        Mockito.when(categoryService.exportCategoriesToJson()).thenReturn(mockData);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/categories/export"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"categories.json\""))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Electronics")))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(testTime.toString())));

        // THEN
        Mockito.verify(categoryService).exportCategoriesToJson();
    }

    @Test
    void testImportCategories() throws Exception {
        // GIVEN
        String jsonContent = String.format(
                "[{\"categoryName\":\"Imported\",\"createdAt\":\"%s\",\"updatedAt\":\"%s\"}]",
                testTime, testTime);
        byte[] jsonData = jsonContent.getBytes();

        CategoryDTO importedCategory = new CategoryDTO(1L, "Imported", null, testTime, testTime);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "categories.json",
                MediaType.APPLICATION_JSON_VALUE,
                jsonData);

        Mockito.when(categoryService.importCategoriesFromJson(jsonData))
                .thenReturn(List.of(importedCategory));

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.multipart("/categories/import")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].categoryName").value("Imported"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].updatedAt").exists());

        // THEN
        Mockito.verify(categoryService).importCategoriesFromJson(jsonData);
    }

    @Test
    void testPartialUpdateCategory() throws Exception {
        // GIVEN
        CategoryCreateDTO updateDTO = new CategoryCreateDTO("Partially Updated", null);
        Category existingCategory = new Category("Electronics", null);
        existingCategory.setCategoryId(1L);
        existingCategory.setCreatedAt(testTime);
        existingCategory.setUpdatedAt(testTime.plusHours(1));

        CategoryDTO categoryDTO = new CategoryDTO(
                1L, "Partially Updated", null, testTime, testTime.plusHours(1));

        Mockito.when(categoryService.getCategoryById(1L)).thenReturn(existingCategory);
        Mockito.doNothing().when(categoryService).updateCategory(ArgumentMatchers.any(Category.class));
        Mockito.when(categoryMapper.toDto(existingCategory)).thenReturn(categoryDTO);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.patch("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryName").value("Partially Updated"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").exists());

        // THEN
        Mockito.verify(categoryService).getCategoryById(1L);
        Mockito.verify(categoryService).updateCategory(ArgumentMatchers.any(Category.class));
        Mockito.verify(categoryMapper).toDto(existingCategory);
    }
}