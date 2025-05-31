package com.example.mapper;

import com.example.dto.PriceDTO;
import com.example.entity.Price;
import com.example.entity.Product;
import com.example.entity.Store;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PriceMapperTest {

    private final PriceMapper priceMapper = Mappers.getMapper(PriceMapper.class);

    @Test
    void testToEntity() {
        // GIVEN
        PriceDTO dto = new PriceDTO(
                1L,
                2L,
                1L,
                9999,
                LocalDateTime.now()
        );

        // WHEN
        Price entity = priceMapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertEquals(dto.getPriceId(), entity.getPriceId());
        assertEquals(dto.getPrice(), entity.getPrice());
        assertEquals(dto.getRecordedAt(), entity.getRecordedAt());
        assertNotNull(entity.getStore());
        assertEquals(dto.getStoreId(), entity.getStore().getStoreId());
        assertNotNull(entity.getProduct());
        assertEquals(dto.getProductId(), entity.getProduct().getProductId());
    }

    @Test
    void testToDto() {
        // GIVEN
        Store store = new Store();
        store.setStoreId(1L);

        Product product = new Product();
        product.setProductId(2L);

        Price entity = new Price(product, store, 14999);
        entity.setPriceId(1L);
        entity.setRecordedAt(LocalDateTime.now());

        // WHEN
        PriceDTO dto = priceMapper.toDto(entity);

        // THEN
        assertNotNull(dto);
        assertEquals(entity.getPriceId(), dto.getPriceId());
        assertEquals(entity.getPrice(), dto.getPrice());
        assertEquals(entity.getRecordedAt(), dto.getRecordedAt());
        assertEquals(store.getStoreId(), dto.getStoreId());
        assertEquals(product.getProductId(), dto.getProductId());
    }

    @Test
    void testToEntityList() {
        // GIVEN
        PriceDTO dto1 = new PriceDTO(1L, 1L, 1L, 10000, LocalDateTime.now());
        PriceDTO dto2 = new PriceDTO(2L, 2L, 2L, 20000, LocalDateTime.now());
        List<PriceDTO> dtos = List.of(dto1, dto2);

        // WHEN
        List<Price> entities = priceMapper.toEntityList(dtos);

        // THEN
        assertEquals(2, entities.size());
        assertEquals(dto1.getPriceId(), entities.get(0).getPriceId());
        assertEquals(dto2.getPriceId(), entities.get(1).getPriceId());
        assertEquals(dto1.getStoreId(), entities.get(0).getStore().getStoreId());
        assertEquals(dto2.getProductId(), entities.get(1).getProduct().getProductId());
    }

    @Test
    void testToDtoList() {
        // GIVEN
        Store store1 = new Store();
        store1.setStoreId(1L);
        Product product1 = new Product();
        product1.setProductId(1L);

        Store store2 = new Store();
        store2.setStoreId(2L);
        Product product2 = new Product();
        product2.setProductId(2L);

        Price entity1 = new Price(product1, store1, 10000);
        entity1.setPriceId(1L);
        entity1.setRecordedAt(LocalDateTime.now());

        Price entity2 = new Price(product2, store2, 20000);
        entity2.setPriceId(2L);
        entity2.setRecordedAt(LocalDateTime.now());

        List<Price> entities = List.of(entity1, entity2);

        // WHEN
        List<PriceDTO> dtos = priceMapper.toDtoList(entities);

        // THEN
        assertEquals(2, dtos.size());
        assertEquals(entity1.getPriceId(), dtos.get(0).getPriceId());
        assertEquals(entity2.getPriceId(), dtos.get(1).getPriceId());
        assertEquals(store1.getStoreId(), dtos.get(0).getStoreId());
        assertEquals(product2.getProductId(), dtos.get(1).getProductId());
    }

    @Test
    void testToEntityShouldThrowExceptionWhenStoreOrProductIsNull() {
        // GIVEN
        PriceDTO dto1 = new PriceDTO(1L, null, 1L, 10000, LocalDateTime.now());
        PriceDTO dto2 = new PriceDTO(2L, 1L, null, 20000, LocalDateTime.now());

        // WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> priceMapper.toEntity(dto1));
        assertThrows(IllegalArgumentException.class, () -> priceMapper.toEntity(dto2));
    }

    @Test
    void testToEntityShouldSetRecordedAtAutomaticallyIfNull() {
        // GIVEN
        PriceDTO dto = new PriceDTO(1L, 1L, 1L, 10000, null);

        // WHEN
        Price entity = priceMapper.toEntity(dto);

        // THEN
        assertNotNull(entity.getRecordedAt());
    }

    @Test
    void testConstructorSetsRecordedAt() {
        // GIVEN
        Product product = new Product();
        Store store = new Store();

        // WHEN
        Price entity = new Price(product, store, 10000);

        // THEN
        assertNotNull(entity.getRecordedAt());
    }

    @Test
    void testIntegerPriceHandling() {
        // GIVEN
        PriceDTO dto = new PriceDTO(1L, 1L, 1L, 12345, LocalDateTime.now());

        // WHEN
        Price entity = priceMapper.toEntity(dto);

        // THEN
        assertEquals(dto.getPrice(), entity.getPrice());
    }
}