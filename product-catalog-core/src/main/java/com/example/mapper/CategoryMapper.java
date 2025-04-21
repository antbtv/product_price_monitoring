package com.example.mapper;

import com.example.entity.Category;
import com.example.dto.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(source = "parentId", target = "parent.categoryId")
    Category toEntity(CategoryDTO categoryDTO);

    @Mapping(source = "parent.categoryId", target = "parentId")
    CategoryDTO toDto(Category category);

    List<Category> toEntityList(List<CategoryDTO> categoryDTOs);

    List<CategoryDTO> toDtoList(List<Category> categories);
}