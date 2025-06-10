package com.example.mapper;

import com.example.dto.ProductDTO;
import com.example.entity.Category;
import com.example.entity.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ProductMapperTest {

    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

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
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(dto.getProductId(), entity.getProductId());
        Assertions.assertEquals(dto.getProductName(), entity.getProductName());
        Assertions.assertNotNull(entity.getCategory());
        Assertions.assertEquals(dto.getCategoryId(), entity.getCategory().getCategoryId());
        Assertions.assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
        Assertions.assertEquals(dto.getUpdatedAt(), entity.getUpdatedAt());
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
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(entity.getProductId(), dto.getProductId());
        Assertions.assertEquals(entity.getProductName(), dto.getProductName());
        Assertions.assertEquals(category.getCategoryId(), dto.getCategoryId());
        Assertions.assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        Assertions.assertEquals(entity.getUpdatedAt(), dto.getUpdatedAt());
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
        Assertions.assertEquals(2, entities.size());
        Assertions.assertEquals(dto1.getProductId(), entities.get(0).getProductId());
        Assertions.assertEquals(dto2.getProductId(), entities.get(1).getProductId());
        Assertions.assertEquals(dto1.getCategoryId(), entities.get(0).getCategory().getCategoryId());
        Assertions.assertEquals(dto2.getProductName(), entities.get(1).getProductName());
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
        Assertions.assertEquals(2, dtos.size());
        Assertions.assertEquals(entity1.getProductId(), dtos.get(0).getProductId());
        Assertions.assertEquals(entity2.getProductId(), dtos.get(1).getProductId());
        Assertions.assertEquals(category1.getCategoryId(), dtos.get(0).getCategoryId());
        Assertions.assertEquals(entity2.getProductName(), dtos.get(1).getProductName());
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
        Assertions.assertThrows(IllegalArgumentException.class, () -> productMapper.toEntity(dto));
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
        Assertions.assertNotNull(entity.getCreatedAt());
        Assertions.assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void testConstructorSetsTimestamps() {
        // GIVEN
        Category category = new Category();
        category.setCategoryId(1L);

        // WHEN
        Product entity = new Product("Стандартный продукт", category);

        // THEN
        Assertions.assertNotNull(entity.getCreatedAt());
        Assertions.assertNotNull(entity.getUpdatedAt());
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
        Assertions.assertNotNull(entity);
        Assertions.assertNull(entity.getProductId());
        Assertions.assertEquals("Базовый продукт", entity.getProductName());
        Assertions.assertNotNull(entity.getCategory());
        Assertions.assertEquals(1L, entity.getCategory().getCategoryId());
    }
}