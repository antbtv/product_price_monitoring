package com.example.mapper;

import com.example.dto.PriceHistoryDTO;
import com.example.entity.PriceHistory;
import com.example.entity.Product;
import com.example.entity.Store;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class PriceHistoryMapperTest {

    private final PriceHistoryMapper priceHistoryMapper = Mappers.getMapper(PriceHistoryMapper.class);

    @Test
    void testToEntity() {
        // GIVEN
        PriceHistoryDTO dto = new PriceHistoryDTO(
                1L,
                1L,
                1L,
                100,
                LocalDateTime.now()
        );

        // WHEN
        PriceHistory entity = priceHistoryMapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertEquals(dto.getPriceHistoryId(), entity.getPriceHistoryId());
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

        PriceHistory entity = new PriceHistory(product, store, 100);
        entity.setPriceHistoryId(1L);
        entity.setRecordedAt(LocalDateTime.now());

        // WHEN
        PriceHistoryDTO dto = priceHistoryMapper.toDto(entity);

        // THEN
        assertNotNull(dto);
        assertEquals(entity.getPriceHistoryId(), dto.getPriceHistoryId());
        assertEquals(entity.getPrice(), dto.getPrice());
        assertEquals(entity.getRecordedAt(), dto.getRecordedAt());
        assertEquals(store.getStoreId(), dto.getStoreId());
        assertEquals(product.getProductId(), dto.getProductId());
    }

    @Test
    void testToEntityList() {
        // GIVEN
        PriceHistoryDTO dto1 = new PriceHistoryDTO(1L, 1L, 1L, 100, LocalDateTime.now());
        PriceHistoryDTO dto2 = new PriceHistoryDTO(2L, 2L, 2L, 100, LocalDateTime.now());
        List<PriceHistoryDTO> dtos = List.of(dto1, dto2);

        // WHEN
        List<PriceHistory> entities = priceHistoryMapper.toEntityList(dtos);

        // THEN
        assertEquals(2, entities.size());
        assertEquals(dto1.getPriceHistoryId(), entities.get(0).getPriceHistoryId());
        assertEquals(dto2.getPriceHistoryId(), entities.get(1).getPriceHistoryId());
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

        PriceHistory entity1 = new PriceHistory(product1, store1, 100);
        entity1.setPriceHistoryId(1L);
        entity1.setRecordedAt(LocalDateTime.now());

        PriceHistory entity2 = new PriceHistory(product2, store2, 200);
        entity2.setPriceHistoryId(2L);
        entity2.setRecordedAt(LocalDateTime.now());

        List<PriceHistory> entities = List.of(entity1, entity2);

        // WHEN
        List<PriceHistoryDTO> dtos = priceHistoryMapper.toDtoList(entities);

        // THEN
        assertEquals(2, dtos.size());
        assertEquals(entity1.getPriceHistoryId(), dtos.get(0).getPriceHistoryId());
        assertEquals(entity2.getPriceHistoryId(), dtos.get(1).getPriceHistoryId());
        assertEquals(store1.getStoreId(), dtos.get(0).getStoreId());
        assertEquals(product2.getProductId(), dtos.get(1).getProductId());
    }

    @Test
    void testConstructorSetsRecordedAt() {
        // GIVEN
        Product product = new Product();
        Store store = new Store();

        // WHEN
        PriceHistory entity = new PriceHistory(product, store, 100);

        // THEN
        assertNotNull(entity.getRecordedAt());
    }
}