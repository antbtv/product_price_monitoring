package com.example.mapper;

import com.example.dto.ProductDTO;
import com.example.entity.Product;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "categoryId", target = "category.categoryId")
    Product toEntity(ProductDTO productDTO);

    @Mapping(source = "category.categoryId", target = "categoryId")
    ProductDTO toDto(Product product);

    List<Product> toEntityList(List<ProductDTO> productDTOS);

    List<ProductDTO> toDtoList(List<Product> products);

    @AfterMapping
    default void setTimestampsIfNull(@MappingTarget Product product) {
        if (product.getCreatedAt() == null) {
            product.setCreatedAt(LocalDateTime.now());
        }
        if (product.getUpdatedAt() == null) {
            product.setUpdatedAt(LocalDateTime.now());
        }
    }

    @BeforeMapping
    default void validateCategory(ProductDTO dto) {
        if (dto.getCategoryId() == null) {
            throw new IllegalArgumentException("Категория не может быть null");
        }
        if (dto.getProductName() == null) {
            throw new IllegalArgumentException("Имя продукта не может быть null");
        }
    }
}