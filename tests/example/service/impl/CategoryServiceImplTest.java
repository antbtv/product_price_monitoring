package com.example.service.impl;

import com.example.repository.CategoryDao;
import com.example.dto.CategoryDTO;
import com.example.entity.Category;
import com.example.mapper.CategoryMapper;
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
class CategoryServiceImplTest {

    @Mock
    private CategoryDao categoryDao;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        categoryService = new CategoryServiceImpl(categoryDao, categoryMapper, objectMapper);
    }

    @Test
    void testCreateCategory() {
        // GIVEN
        Category category = new Category("Test Category", null);
        Mockito.when(categoryDao.create(category)).thenReturn(category);

        // WHEN
        Category result = categoryService.createCategory(category);

        // THEN
        Assertions.assertEquals(category, result);
        Mockito.verify(categoryDao).create(category);
    }

    @Test
    void testGetCategoryById() {
        // GIVEN
        Long id = 1L;
        Category category = new Category("Test Category", null);
        Mockito.when(categoryDao.findById(id)).thenReturn(category);

        // WHEN
        Category result = categoryService.getCategoryById(id);

        // THEN
        Assertions.assertEquals(category, result);
        Mockito.verify(categoryDao).findById(id);
    }

    @Test
    void testUpdateCategory() {
        // GIVEN
        Category category = new Category("Test Category", null);
        category.setCategoryId(1L);
        Mockito.when(categoryDao.findById(1L)).thenReturn(category);

        // WHEN
        categoryService.updateCategory(category);

        // THEN
        Mockito.verify(categoryDao).update(category);
    }

    @Test
    void testDeleteCategory() {
        // GIVEN
        Category category = new Category("Test Category", null);
        category.setCategoryId(1L);
        Mockito.when(categoryDao.findById(1L)).thenReturn(category);

        // WHEN
        categoryService.deleteCategory(1L);

        // THEN
        Mockito.verify(categoryDao).delete(1L);
    }

    @Test
    void testGetAllCategories() {
        // GIVEN
        Category category1 = new Category("Category 1", null);
        Category category2 = new Category("Category 2", null);
        Mockito.when(categoryDao.findAll()).thenReturn(List.of(category1, category2));

        // WHEN
        List<Category> result = categoryService.getAllCategories();

        // THEN
        Assertions.assertEquals(2, result.size());
        Mockito.verify(categoryDao).findAll();
    }

    @Test
    void testExportCategoriesToJson() throws IOException {
        // GIVEN
        Category category = new Category("Test Category", null);
        Mockito.when(categoryDao.findAll()).thenReturn(List.of(category));

        // WHEN
        byte[] result = categoryService.exportCategoriesToJson();

        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.length > 0);
    }

    @Test
    void testImportCategoriesFromJson() throws IOException {
        // GIVEN
        String jsonData = "[{\"categoryName\":\"Category\", \"parentId\": null}]";
        byte[] data = jsonData.getBytes();

        Category category = new Category();
        category.setCategoryName("Category");
        category.setParent(null);

        Mockito.when(categoryDao.create(ArgumentMatchers.any(Category.class))).thenReturn(category);
        Mockito.when(categoryMapper.toEntityList(ArgumentMatchers.anyList())).thenReturn(List.of(category));

        // WHEN
        List<CategoryDTO> result = categoryService.importCategoriesFromJson(data);

        // THEN
        Assertions.assertEquals(1, result.size());
        Assertions.assertNull(result.get(0).getParentId());
        Assertions.assertEquals("Category", result.get(0).getCategoryName());
        Mockito.verify(categoryDao).create(ArgumentMatchers.any(Category.class));
    }
}
