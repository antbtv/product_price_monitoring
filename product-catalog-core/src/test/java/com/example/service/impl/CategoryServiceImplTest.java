package com.example.service.impl;

import com.example.dto.CategoryDTO;
import com.example.entity.Category;
import com.example.mapper.CategoryMapper;
import com.example.repository.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        categoryService = new CategoryServiceImpl(categoryRepository, categoryMapper, objectMapper);
    }

    @Test
    void testCreateCategory() {
        // GIVEN
        Category category = new Category("Test Category", null);
        when(categoryRepository.save(category)).thenReturn(category);

        // WHEN
        Category result = categoryService.createCategory(category);

        // THEN
        assertEquals(category, result);
        verify(categoryRepository).save(category);
    }

    @Test
    void testGetCategoryById() {
        // GIVEN
        Long id = 1L;
        Category category = new Category("Test Category", null);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        // WHEN
        Category result = categoryService.getCategoryById(id);

        // THEN
        assertEquals(category, result);
        verify(categoryRepository).findById(id);
    }

    @Test
    void testUpdateCategory() {
        // GIVEN
        Category category = new Category("Test Category", null);
        category.setCategoryId(1L);
        when(categoryRepository.existsById(1L)).thenReturn(true);

        // WHEN
        categoryService.updateCategory(category);

        // THEN
        verify(categoryRepository).save(category);
    }

    @Test
    void testDeleteCategory() {
        // GIVEN
        Long id = 1L;
        when(categoryRepository.existsById(id)).thenReturn(true);

        // WHEN
        categoryService.deleteCategory(id);

        // THEN
        verify(categoryRepository).deleteById(id);
    }

    @Test
    void testGetAllCategories() {
        // GIVEN
        Category category1 = new Category("Category 1", null);
        Category category2 = new Category("Category 2", null);
        when(categoryRepository.findAllWithSubCategories()).thenReturn(List.of(category1, category2));

        // WHEN
        List<Category> result = categoryService.getAllCategories();

        // THEN
        assertEquals(2, result.size());
        verify(categoryRepository).findAllWithSubCategories();
    }

    @Test
    void testExportCategoriesToJson() {
        // GIVEN
        Category category = new Category("Test Category", null);
        when(categoryRepository.findAllWithSubCategories()).thenReturn(List.of(category));

        // WHEN
        byte[] result = categoryService.exportCategoriesToJson();

        // THEN
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testImportCategoriesFromJson() {
        // GIVEN
        String jsonData = "[{\"categoryName\":\"Category\", \"parentId\": null}]";
        byte[] data = jsonData.getBytes();
        Category category = new Category();
        category.setCategoryName("Category");
        category.setParent(null);
        when(categoryRepository.saveAll(anyList())).thenReturn(List.of(category));
        when(categoryMapper.toEntityList(anyList())).thenReturn(List.of(category));

        // WHEN
        List<CategoryDTO> result = categoryService.importCategoriesFromJson(data);

        // THEN
        assertEquals(1, result.size());
        assertNull(result.get(0).getParentId());
        assertEquals("Category", result.get(0).getCategoryName());
        verify(categoryRepository).saveAll(anyList());
    }
}
