package com.example.controller;

import com.example.dto.CategoryCreateDTO;
import com.example.dto.CategoryDTO;
import com.example.entity.Category;
import com.example.mapper.CategoryMapper;
import com.example.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

        when(categoryService.getCategoryById(1L)).thenReturn(parent);
        when(categoryService.createCategory(any(Category.class))).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDTO);

        // WHEN
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryId").value(2L))
                .andExpect(jsonPath("$.categoryName").value("Electronics"))
                .andExpect(jsonPath("$.parentId").value(1L))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        // THEN
        verify(categoryService).getCategoryById(1L);
        verify(categoryService).createCategory(any(Category.class));
        verify(categoryMapper).toDto(category);
    }

    @Test
    void testGetCategoryById() throws Exception {
        // GIVEN
        Category category = new Category("Electronics", null);
        category.setCategoryId(1L);
        category.setCreatedAt(testTime);
        category.setUpdatedAt(testTime);

        CategoryDTO categoryDTO = new CategoryDTO(1L, "Electronics", null, testTime, testTime);

        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDTO);

        // WHEN
        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1L))
                .andExpect(jsonPath("$.categoryName").value("Electronics"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        // THEN
        verify(categoryService).getCategoryById(1L);
        verify(categoryMapper).toDto(category);
    }

    @Test
    void testUpdateCategory() throws Exception {
        // GIVEN
        Category category = new Category("Updated Electronics", null);
        category.setCategoryId(1L);
        category.setCreatedAt(testTime);
        category.setUpdatedAt(testTime.plusHours(1));

        CategoryDTO categoryDTO = new CategoryDTO(1L, "Updated Electronics", null, testTime, testTime.plusHours(1));

        doNothing().when(categoryService).updateCategory(any(Category.class));
        when(categoryMapper.toDto(any(Category.class))).thenReturn(categoryDTO);

        // WHEN
        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1L))
                .andExpect(jsonPath("$.categoryName").value("Updated Electronics"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        // THEN
        verify(categoryService).updateCategory(any(Category.class));
        verify(categoryMapper).toDto(any(Category.class));
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

        when(categoryService.getAllCategories()).thenReturn(categories);
        when(categoryMapper.toDtoList(categories)).thenReturn(categoryDTOs);

        // WHEN
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].categoryName").value("Electronics"))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[1].categoryName").value("Books"))
                .andExpect(jsonPath("$[1].updatedAt").exists());

        // THEN
        verify(categoryService).getAllCategories();
        verify(categoryMapper).toDtoList(categories);
    }

    @Test
    void testExportCategories() throws Exception {
        // GIVEN
        byte[] mockData = ("{\"categories\":[{\"categoryName\":\"Electronics\",\"createdAt\":\"" +
                testTime + "\"}]}").getBytes();
        when(categoryService.exportCategoriesToJson()).thenReturn(mockData);

        // WHEN
        mockMvc.perform(get("/categories/export"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"categories.json\""))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Electronics")))
                .andExpect(content().string(containsString(testTime.toString())));

        // THEN
        verify(categoryService).exportCategoriesToJson();
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

        when(categoryService.importCategoriesFromJson(jsonData))
                .thenReturn(List.of(importedCategory));

        // WHEN
        mockMvc.perform(multipart("/categories/import")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("Imported"))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].updatedAt").exists());

        // THEN
        verify(categoryService).importCategoriesFromJson(jsonData);
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

        when(categoryService.getCategoryById(1L)).thenReturn(existingCategory);
        doNothing().when(categoryService).updateCategory(any(Category.class));
        when(categoryMapper.toDto(existingCategory)).thenReturn(categoryDTO);

        // WHEN
        mockMvc.perform(patch("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("Partially Updated"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        // THEN
        verify(categoryService).getCategoryById(1L);
        verify(categoryService).updateCategory(any(Category.class));
        verify(categoryMapper).toDto(existingCategory);
    }
}