package com.example.mapper;

import com.example.dto.CategoryDTO;
import com.example.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryMapperTest {

    @InjectMocks
    private CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

    @Test
    void testToEntity() {
        // GIVEN
        CategoryDTO categoryDTO = new CategoryDTO(1L, "Электроника", 2L,
                LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        // WHEN
        Category result = categoryMapper.toEntity(categoryDTO);

        // THEN
        Assertions.assertEquals(categoryDTO.getCategoryId(), result.getCategoryId());
        Assertions.assertEquals(categoryDTO.getCategoryName(), result.getCategoryName());
        Assertions.assertNotNull(result.getParent());
        Assertions.assertEquals(categoryDTO.getParentId(), result.getParent().getCategoryId());
        Assertions.assertNotNull(result.getCreatedAt());
        Assertions.assertNotNull(result.getUpdatedAt());
    }

    @Test
    void testToDto() {
        // GIVEN
        Category parent = new Category();
        parent.setCategoryId(2L);

        Category category = new Category("Электроника", parent);
        category.setCategoryId(1L);
        category.setSubCategories(new ArrayList<>());

        // WHEN
        CategoryDTO result = categoryMapper.toDto(category);

        // THEN
        Assertions.assertEquals(category.getCategoryId(), result.getCategoryId());
        Assertions.assertEquals(category.getCategoryName(), result.getCategoryName());
        Assertions.assertEquals(parent.getCategoryId(), result.getParentId());
    }

    @Test
    void testToEntityList() {
        // GIVEN
        CategoryDTO categoryDTO1 = new CategoryDTO(1L, "Электроника", 3L,
                LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        CategoryDTO categoryDTO2 = new CategoryDTO(2L, "Бытовая техника", 3L,
                LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        List<CategoryDTO> categoryDTOs = Arrays.asList(categoryDTO1, categoryDTO2);

        // WHEN
        List<Category> result = categoryMapper.toEntityList(categoryDTOs);

        // THEN
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(categoryDTO1.getCategoryId(), result.get(0).getCategoryId());
        Assertions.assertEquals(categoryDTO2.getCategoryId(), result.get(1).getCategoryId());
        Assertions.assertEquals(categoryDTO1.getParentId(), result.get(0).getParent().getCategoryId());
        Assertions.assertNotNull(result.get(0).getCreatedAt());
        Assertions.assertNotNull(result.get(1).getCreatedAt());
    }

    @Test
    void testToDtoList() {
        // GIVEN
        Category parent = new Category();
        parent.setCategoryId(3L);

        Category category1 = new Category("Электроника", parent);
        category1.setCategoryId(1L);
        category1.setSubCategories(new ArrayList<>());

        Category category2 = new Category("Бытовая техника", parent);
        category2.setCategoryId(2L);
        category2.setSubCategories(new ArrayList<>());

        List<Category> categories = Arrays.asList(category1, category2);

        // WHEN
        List<CategoryDTO> result = categoryMapper.toDtoList(categories);

        // THEN
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(category1.getCategoryId(), result.get(0).getCategoryId());
        Assertions.assertEquals(category2.getCategoryId(), result.get(1).getCategoryId());
        Assertions.assertEquals(parent.getCategoryId(), result.get(0).getParentId());
        Assertions.assertEquals(parent.getCategoryId(), result.get(1).getParentId());
    }

    @Test
    void testToDtoWithNullParent() {
        // GIVEN
        Category category = new Category("Электроника", null);
        category.setCategoryId(1L);
        category.setSubCategories(new ArrayList<>());

        // WHEN
        CategoryDTO result = categoryMapper.toDto(category);

        // THEN
        Assertions.assertEquals(category.getCategoryId(), result.getCategoryId());
        Assertions.assertEquals(category.getCategoryName(), result.getCategoryName());
        Assertions.assertNull(result.getParentId());
    }

    @Test
    void testToEntityWithSubCategories() {
        // GIVEN
        CategoryDTO categoryDTO = new CategoryDTO(1L, "Электроника", null,
                LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        // WHEN
        Category result = categoryMapper.toEntity(categoryDTO);

        // THEN
        Assertions.assertEquals(categoryDTO.getCategoryId(), result.getCategoryId());
        Assertions.assertNotNull(result.getSubCategories());
        Assertions.assertEquals(0, result.getSubCategories().size());
    }
}