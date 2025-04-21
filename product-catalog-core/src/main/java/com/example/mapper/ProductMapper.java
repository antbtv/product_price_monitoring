package com.example.mapper;

import com.example.dto.ProductDTO;
import com.example.dto.StoreDTO;
import com.example.entity.Product;
import com.example.entity.Store;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "categoryId", target = "category.categoryId")
    Product toEntity(ProductDTO productDTO);

    @Mapping(source = "category.categoryId", target = "categoryId")
    ProductDTO toDto(Product product);

    List<Product> toEntityList(List<ProductDTO> productDTOS);

    List<ProductDTO> toDtoList(List<Product> products);
}