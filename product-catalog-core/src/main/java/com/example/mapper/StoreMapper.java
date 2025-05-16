package com.example.mapper;

import com.example.dto.StoreDTO;
import com.example.entity.Price;
import com.example.entity.Store;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    StoreMapper INSTANCE = Mappers.getMapper(StoreMapper.class);

    Store toEntity(StoreDTO storeDTO);

    StoreDTO toDto(Store store);

    List<Store> toEntityList(List<StoreDTO> storeDTOS);

    List<StoreDTO> toDtoList(List<Store> stores);

    @AfterMapping
    default void setTimestampsIfNull(@MappingTarget Store store) {
        if (store.getCreatedAt() == null) {
            store.setCreatedAt(LocalDateTime.now());
        }
        if (store.getUpdatedAt() == null) {
            store.setUpdatedAt(LocalDateTime.now());
        }
    }
}