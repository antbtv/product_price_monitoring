package com.example.mapper;

import com.example.dto.PriceDTO;
import com.example.dto.PriceHistoryDTO;
import com.example.entity.Price;
import com.example.entity.PriceHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceHistoryMapper {

    @Mapping(source = "storeId", target = "store.storeId")
    @Mapping(source = "productId", target = "product.productId")
    PriceHistory toEntity(PriceHistoryDTO priceHistoryDTO);

    @Mapping(source = "store.storeId", target = "storeId")
    @Mapping(source = "product.productId", target = "productId")
    PriceHistoryDTO toDto(PriceHistory priceHistory);

    List<PriceHistory> toEntityList(List<PriceHistoryDTO> priceHistoryDTOS);

    List<PriceHistoryDTO> toDtoList(List<PriceHistory> priceHistories);
}