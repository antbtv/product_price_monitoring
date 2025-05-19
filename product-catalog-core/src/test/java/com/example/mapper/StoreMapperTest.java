package com.example.mapper;

import com.example.dto.StoreDTO;
import com.example.entity.Store;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StoreMapperTest {

    private final StoreMapper storeMapper = Mappers.getMapper(StoreMapper.class);

    @Test
    void testToEntity() {
        // GIVEN
        LocalDateTime testTime = LocalDateTime.now();
        StoreDTO dto = new StoreDTO(
                1L,
                "Главный магазин",
                "ул. Центральная, 1",
                testTime,
                testTime
        );

        // WHEN
        Store entity = storeMapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertEquals(dto.getStoreId(), entity.getStoreId());
        assertEquals(dto.getStoreName(), entity.getStoreName());
        assertEquals(dto.getAddress(), entity.getAddress());
        assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
        assertEquals(dto.getUpdatedAt(), entity.getUpdatedAt());
    }

    @Test
    void testToDto() {
        // GIVEN
        LocalDateTime testTime = LocalDateTime.now();
        Store entity = new Store("Филиал на окраине", "ул. Крайняя, 100");
        entity.setStoreId(2L);
        entity.setCreatedAt(testTime);
        entity.setUpdatedAt(testTime);

        // WHEN
        StoreDTO dto = storeMapper.toDto(entity);

        // THEN
        assertNotNull(dto);
        assertEquals(entity.getStoreId(), dto.getStoreId());
        assertEquals(entity.getStoreName(), dto.getStoreName());
        assertEquals(entity.getAddress(), dto.getAddress());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), dto.getUpdatedAt());
    }

    @Test
    void testToEntityList() {
        // GIVEN
        LocalDateTime testTime = LocalDateTime.now();
        StoreDTO dto1 = new StoreDTO(1L, "Магазин 1", "Адрес 1", testTime, testTime);
        StoreDTO dto2 = new StoreDTO(2L, "Магазин 2", "Адрес 2", testTime, testTime);
        List<StoreDTO> dtos = List.of(dto1, dto2);

        // WHEN
        List<Store> entities = storeMapper.toEntityList(dtos);

        // THEN
        assertEquals(2, entities.size());
        assertEquals(dto1.getStoreId(), entities.get(0).getStoreId());
        assertEquals(dto2.getStoreId(), entities.get(1).getStoreId());
        assertEquals(dto1.getStoreName(), entities.get(0).getStoreName());
        assertEquals(dto2.getAddress(), entities.get(1).getAddress());
    }

    @Test
    void testToDtoList() {
        // GIVEN
        LocalDateTime testTime = LocalDateTime.now();
        Store entity1 = new Store("Торговая точка A", "Адрес A");
        entity1.setStoreId(1L);
        entity1.setCreatedAt(testTime);
        entity1.setUpdatedAt(testTime);

        Store entity2 = new Store("Торговая точка B", "Адрес B");
        entity2.setStoreId(2L);
        entity2.setCreatedAt(testTime);
        entity2.setUpdatedAt(testTime);

        List<Store> entities = List.of(entity1, entity2);

        // WHEN
        List<StoreDTO> dtos = storeMapper.toDtoList(entities);

        // THEN
        assertEquals(2, dtos.size());
        assertEquals(entity1.getStoreId(), dtos.get(0).getStoreId());
        assertEquals(entity2.getStoreId(), dtos.get(1).getStoreId());
        assertEquals(entity1.getAddress(), dtos.get(0).getAddress());
        assertEquals(entity2.getStoreName(), dtos.get(1).getStoreName());
    }

    @Test
    void testToEntityWithNullValues() {
        // GIVEN
        StoreDTO dto = new StoreDTO(
                null,
                null,
                null,
                null,
                null
        );

        // WHEN
        Store entity = storeMapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertNull(entity.getStoreId());
        assertNull(entity.getStoreName());
        assertNull(entity.getAddress());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void testToDtoWithNullValues() {
        // GIVEN
        Store entity = new Store(null, null);
        entity.setStoreId(null);
        entity.setCreatedAt(null);
        entity.setUpdatedAt(null);

        // WHEN
        StoreDTO dto = storeMapper.toDto(entity);

        // THEN
        assertNotNull(dto);
        assertNull(dto.getStoreId());
        assertNull(dto.getStoreName());
        assertNull(dto.getAddress());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
    }

    @Test
    void testToEntityWithPartialData() {
        // GIVEN
        StoreDTO dto = new StoreDTO(
                3L,
                "Минимаркет",
                null,
                null,
                null
        );

        // WHEN
        Store entity = storeMapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertEquals(dto.getStoreId(), entity.getStoreId());
        assertEquals(dto.getStoreName(), entity.getStoreName());
        assertNull(entity.getAddress());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void testConstructorSetsTimestamps() {
        // GIVEN
        // WHEN
        Store entity = new Store("Новый магазин", "Новый адрес");

        // THEN
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void testSpecialCharactersInFields() {
        // GIVEN
        StoreDTO dto = new StoreDTO(
                4L,
                "Магазин Рассвет",
                "ул. Победы, д.5/2",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // WHEN
        Store entity = storeMapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertEquals(dto.getStoreName(), entity.getStoreName());
        assertEquals(dto.getAddress(), entity.getAddress());
    }

    @Test
    void testToEntityShouldSetTimestampsAutomaticallyIfNull() {
        // GIVEN
        StoreDTO dto = new StoreDTO(
                5L,
                "Автоматические даты",
                "ул. Тестовая",
                null,
                null
        );

        // WHEN
        Store entity = storeMapper.toEntity(dto);

        // THEN
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }
}