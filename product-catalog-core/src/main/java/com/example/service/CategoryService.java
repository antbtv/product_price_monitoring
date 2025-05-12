package com.example.service;

import com.example.dto.CategoryDTO;
import com.example.dto.StoreDTO;
import com.example.entity.Category;

import java.io.IOException;
import java.util.List;

public interface CategoryService {

    Category createCategory(Category category);

    Category getCategoryById(Long id);

    void updateCategory(Category category);

    void deleteCategory(Long id);

    List<Category> getAllCategories();

    byte[] exportCategoriesToJson() throws IOException;

    List<CategoryDTO> importCategoriesFromJson(byte[] data) throws IOException;
}
