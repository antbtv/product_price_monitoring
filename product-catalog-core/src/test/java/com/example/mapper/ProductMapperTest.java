package com.example.mapper;

import com.example.dto.ProductDTO;
import com.example.entity.Category;
import com.example.entity.Product;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductMapperTest {

    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    @Test
    void testToEntity() {
        // GIVEN
        LocalDateTime testTime = LocalDateTime.now();
        ProductDTO dto = new ProductDTO(
                1L,
                "Универсальный продукт A",
                3L,
                testTime,
                testTime
        );

        // WHEN
        Product entity = productMapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertEquals(dto.getProductId(), entity.getProductId());
        assertEquals(dto.getProductName(), entity.getProductName());
        assertNotNull(entity.getCategory());
        assertEquals(dto.getCategoryId(), entity.getCategory().getCategoryId());
        assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
        assertEquals(dto.getUpdatedAt(), entity.getUpdatedAt());
    }

    @Test
    void testToDto() {
        // GIVEN
        LocalDateTime testTime = LocalDateTime.now();
        Category category = new Category();
        category.setCategoryId(3L);

        Product entity = new Product("Базовый продукт B", category);
        entity.setProductId(2L);
        entity.setCreatedAt(testTime);
        entity.setUpdatedAt(testTime);

        // WHEN
        ProductDTO dto = productMapper.toDto(entity);

        // THEN
        assertNotNull(dto);
        assertEquals(entity.getProductId(), dto.getProductId());
        assertEquals(entity.getProductName(), dto.getProductName());
        assertEquals(category.getCategoryId(), dto.getCategoryId());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), dto.getUpdatedAt());
    }

    @Test
    void testToEntityList() {
        // GIVEN
        LocalDateTime testTime = LocalDateTime.now();
        ProductDTO dto1 = new ProductDTO(1L, "Продукт группы 1", 1L, testTime, testTime);
        ProductDTO dto2 = new ProductDTO(2L, "Продукт группы 2", 2L, testTime, testTime);
        List<ProductDTO> dtos = List.of(dto1, dto2);

        // WHEN
        List<Product> entities = productMapper.toEntityList(dtos);

        // THEN
        assertEquals(2, entities.size());
        assertEquals(dto1.getProductId(), entities.get(0).getProductId());
        assertEquals(dto2.getProductId(), entities.get(1).getProductId());
        assertEquals(dto1.getCategoryId(), entities.get(0).getCategory().getCategoryId());
        assertEquals(dto2.getProductName(), entities.get(1).getProductName());
    }

    @Test
    void testToDtoList() {
        // GIVEN
        LocalDateTime testTime = LocalDateTime.now();
        Category category1 = new Category();
        category1.setCategoryId(1L);

        Category category2 = new Category();
        category2.setCategoryId(2L);

        Product entity1 = new Product("Основной продукт", category1);
        entity1.setProductId(1L);
        entity1.setCreatedAt(testTime);
        entity1.setUpdatedAt(testTime);

        Product entity2 = new Product("Дополнительный продукт", category2);
        entity2.setProductId(2L);
        entity2.setCreatedAt(testTime);
        entity2.setUpdatedAt(testTime);

        List<Product> entities = List.of(entity1, entity2);

        // WHEN
        List<ProductDTO> dtos = productMapper.toDtoList(entities);

        // THEN
        assertEquals(2, dtos.size());
        assertEquals(entity1.getProductId(), dtos.get(0).getProductId());
        assertEquals(entity2.getProductId(), dtos.get(1).getProductId());
        assertEquals(category1.getCategoryId(), dtos.get(0).getCategoryId());
        assertEquals(entity2.getProductName(), dtos.get(1).getProductName());
    }

    @Test
    void testToEntityShouldThrowExceptionWhenCategoryIsNull() {
        // GIVEN
        ProductDTO dto = new ProductDTO(
                1L,
                "Продукт без категории",
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> productMapper.toEntity(dto));
    }

    @Test
    void testToEntityShouldSetTimestampsAutomaticallyIfNull() {
        // GIVEN
        ProductDTO dto = new ProductDTO(
                1L,
                "Новый продукт каталога",
                1L,
                null,
                null
        );

        // WHEN
        Product entity = productMapper.toEntity(dto);

        // THEN
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void testConstructorSetsTimestamps() {
        // GIVEN
        Category category = new Category();
        category.setCategoryId(1L);

        // WHEN
        Product entity = new Product("Стандартный продукт", category);

        // THEN
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void testMinimalProductMapping() {
        // GIVEN
        ProductDTO dto = new ProductDTO(
                null,
                "Базовый продукт",
                1L,
                null,
                null
        );

        // WHEN
        Product entity = productMapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertNull(entity.getProductId());
        assertEquals("Базовый продукт", entity.getProductName());
        assertNotNull(entity.getCategory());
        assertEquals(1L, entity.getCategory().getCategoryId());
    }
}