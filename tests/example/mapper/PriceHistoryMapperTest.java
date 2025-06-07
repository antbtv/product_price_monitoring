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

import static org.junit.jupiter.api.Assertions.*;

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
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(dto.getPriceHistoryId(), entity.getPriceHistoryId());
        Assertions.assertEquals(dto.getPrice(), entity.getPrice());
        Assertions.assertEquals(dto.getRecordedAt(), entity.getRecordedAt());
        Assertions.assertNotNull(entity.getStore());
        Assertions.assertEquals(dto.getStoreId(), entity.getStore().getStoreId());
        Assertions.assertNotNull(entity.getProduct());
        Assertions.assertEquals(dto.getProductId(), entity.getProduct().getProductId());
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
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(entity.getPriceHistoryId(), dto.getPriceHistoryId());
        Assertions.assertEquals(entity.getPrice(), dto.getPrice());
        Assertions.assertEquals(entity.getRecordedAt(), dto.getRecordedAt());
        Assertions.assertEquals(store.getStoreId(), dto.getStoreId());
        Assertions.assertEquals(product.getProductId(), dto.getProductId());
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
        Assertions.assertEquals(2, entities.size());
        Assertions.assertEquals(dto1.getPriceHistoryId(), entities.get(0).getPriceHistoryId());
        Assertions.assertEquals(dto2.getPriceHistoryId(), entities.get(1).getPriceHistoryId());
        Assertions.assertEquals(dto1.getStoreId(), entities.get(0).getStore().getStoreId());
        Assertions.assertEquals(dto2.getProductId(), entities.get(1).getProduct().getProductId());
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
        Assertions.assertEquals(2, dtos.size());
        Assertions.assertEquals(entity1.getPriceHistoryId(), dtos.get(0).getPriceHistoryId());
        Assertions.assertEquals(entity2.getPriceHistoryId(), dtos.get(1).getPriceHistoryId());
        Assertions.assertEquals(store1.getStoreId(), dtos.get(0).getStoreId());
        Assertions.assertEquals(product2.getProductId(), dtos.get(1).getProductId());
    }

    @Test
    void testConstructorSetsRecordedAt() {
        // GIVEN
        Product product = new Product();
        Store store = new Store();

        // WHEN
        PriceHistory entity = new PriceHistory(product, store, 100);

        // THEN
        Assertions.assertNotNull(entity.getRecordedAt());
    }
}