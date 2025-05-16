package com.example.mapper;

import com.example.dto.PriceDTO;
import com.example.dto.ProductDTO;
import com.example.entity.Price;
import com.example.entity.Product;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceMapper {

    PriceMapper INSTANCE = Mappers.getMapper(PriceMapper.class);

    @Mapping(source = "storeId", target = "store.storeId")
    @Mapping(source = "productId", target = "product.productId")
    Price toEntity(PriceDTO priceDTO);

    @Mapping(source = "store.storeId", target = "storeId")
    @Mapping(source = "product.productId", target = "productId")
    PriceDTO toDto(Price price);

    List<Price> toEntityList(List<PriceDTO> priceDTOS);

    List<PriceDTO> toDtoList(List<Price> prices);

    @AfterMapping
    default void setTimestampsIfNull(@MappingTarget Price price) {
        if (price.getRecordedAt() == null) {
            price.setRecordedAt(LocalDateTime.now());
        }
    }

    @BeforeMapping
    default void validatePrice(PriceDTO dto) {
        if (dto.getProductId() == null) {
            throw new IllegalArgumentException("Продукт не может быть null");
        }
        if (dto.getStoreId() == null) {
            throw new IllegalArgumentException("Магазин не может быть null");
        }
        if (dto.getPrice() == null) {
            throw new IllegalArgumentException("Цена не может быть null");
        }
    }
}