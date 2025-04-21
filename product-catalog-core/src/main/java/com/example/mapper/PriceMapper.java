package com.example.mapper;

import com.example.dto.PriceDTO;
import com.example.entity.Price;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

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
}