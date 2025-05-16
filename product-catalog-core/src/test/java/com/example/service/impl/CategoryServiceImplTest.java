package com.example.service.impl;

import com.example.dao.CategoryDao;
import com.example.dto.CategoryDTO;
import com.example.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryDao categoryDao;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void testCreateCategory() {
        // GIVEN
        Category category = new Category("Test Category", null);
        when(categoryDao.create(category)).thenReturn(category);

        // WHEN
        Category result = categoryService.createCategory(category);

        // THEN
        assertEquals(category, result);
        verify(categoryDao).create(category);
    }

    @Test
    void testGetCategoryById() {
        // GIVEN
        Long id = 1L;
        Category category = new Category("Test Category", null);
        when(categoryDao.findById(id)).thenReturn(category);

        // WHEN
        Category result = categoryService.getCategoryById(id);

        // THEN
        assertEquals(category, result);
        verify(categoryDao).findById(id);
    }

    @Test
    void testUpdateCategory() {
        // GIVEN
        Category category = new Category("Test Category", null);

        // WHEN
        categoryService.updateCategory(category);

        // THEN
        verify(categoryDao).update(category);
    }

    @Test
    void testDeleteCategory() {
        // GIVEN
        Long id = 1L;

        // WHEN
        categoryService.deleteCategory(id);

        // THEN
        verify(categoryDao).delete(id);
    }

    @Test
    void testGetAllCategories() {
        // GIVEN
        Category category1 = new Category("Category 1", null);
        Category category2 = new Category("Category 2", null);
        when(categoryDao.findAll()).thenReturn(List.of(category1, category2));

        // WHEN
        List<Category> result = categoryService.getAllCategories();

        // THEN
        assertEquals(2, result.size());
        verify(categoryDao).findAll();
    }

    @Test
    void testExportCategoriesToJson() throws IOException {
        // GIVEN
        Category category = new Category("Test Category", null);
        when(categoryDao.findAll()).thenReturn(List.of(category));

        // WHEN
        byte[] result = categoryService.exportCategoriesToJson();

        // THEN
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testImportCategoriesFromJson() throws IOException {
        // GIVEN
        String jsonData = "[{\"categoryName\":\"Category\", \"parentId\": null}]";
        byte[] data = jsonData.getBytes();

        Category mockCategory = new Category();
        mockCategory.setCategoryName("Category");
        mockCategory.setParent(null);

        when(categoryDao.create(any(Category.class))).thenReturn(mockCategory);

        // WHEN
        List<CategoryDTO> result = categoryService.importCategoriesFromJson(data);

        // THEN
        assertEquals(1, result.size());
        assertNull(result.get(0).getParentId());
        assertEquals("Category", result.get(0).getCategoryName());
        verify(categoryDao).create(any(Category.class));
    }
}
