package com.example.mapper;

import com.example.entity.Category;
import com.example.dto.CategoryDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(source = "parentId", target = "parent.categoryId")
    Category toEntity(CategoryDTO categoryDTO);

    @Mapping(source = "parent.categoryId", target = "parentId")
    CategoryDTO toDto(Category category);

    List<Category> toEntityList(List<CategoryDTO> categoryDTOs);

    List<CategoryDTO> toDtoList(List<Category> categories);

    @AfterMapping
    default void setTimestampsIfNull(@MappingTarget Category category) {
        if (category.getCreatedAt() == null) {
            category.setCreatedAt(LocalDateTime.now());
        }
        if (category.getUpdatedAt() == null) {
            category.setUpdatedAt(LocalDateTime.now());
        }
    }
}