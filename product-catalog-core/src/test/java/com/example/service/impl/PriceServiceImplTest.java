package com.example.service.impl;

import com.example.repository.PriceHistoryRepository;
import com.example.repository.PriceRepository;
import com.example.utils.ChartGenerator;
import com.example.dto.PriceDTO;
import com.example.entity.Price;
import com.example.entity.PriceHistory;
import com.example.entity.Product;
import com.example.entity.Store;
import com.example.mapper.PriceMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceServiceImplTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private PriceHistoryRepository priceHistoryRepository;

    @Mock
    private PriceMapper priceMapper;

    @InjectMocks
    private PriceServiceImpl priceService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        ChartGenerator chartGenerator = new ChartGenerator();
        priceService = new PriceServiceImpl(priceRepository, priceHistoryRepository, chartGenerator,
                priceMapper, objectMapper);
    }

    @Test
    void testCreatePrice() {
        // GIVEN
        Price price = new Price();

        Product product = new Product();
        product.setProductName("Test");
        product.setProductId(1L);

        Store store = new Store();
        store.setStoreId(1L);

        price.setPriceId(1L);
        price.setProduct(product);
        price.setStore(store);
        when(priceRepository.save(price)).thenReturn(price);

        // WHEN
        Price result = priceService.createPrice(price);

        // THEN
        assertEquals(price, result);
        verify(priceRepository).save(price);
    }

    @Test
    void testGetPriceById() {
        // GIVEN
        Price price = new Price();

        Product product = new Product();
        product.setProductName("Test");
        product.setProductId(1L);

        Store store = new Store();
        store.setStoreId(1L);

        price.setPriceId(1L);
        price.setProduct(product);
        price.setStore(store);

        when(priceRepository.findById(1L)).thenReturn(Optional.of(price));

        // WHEN
        Price result = priceService.getPriceById(1L);

        // THEN
        assertEquals(price, result);
        verify(priceRepository).findById(1L);
    }

    @Test
    void testUpdatePrice() {
        // GIVEN
        Price currentPrice = new Price();
        currentPrice.setPriceId(1L);
        currentPrice.setPrice(100);

        Price newPrice = new Price();
        newPrice.setPriceId(1L);
        newPrice.setPrice(120);

        when(priceRepository.findById(currentPrice.getPriceId())).thenReturn(Optional.of(currentPrice));

        // WHEN
        priceService.updatePrice(newPrice);

        // THEN
        verify(priceHistoryRepository).save(ArgumentMatchers.any(PriceHistory.class));
        verify(priceRepository).save(newPrice);
    }

    @Test
    void testDeletePrice() {
        // GIVEN
        Long id = 1L;
        when(priceRepository.existsById(id)).thenReturn(true);

        // WHEN
        priceService.deletePrice(id);

        // THEN
        verify(priceRepository).deleteById(id);
    }

    @Test
    void testGetAllPrices() {
        // GIVEN
        Price price1 = new Price();
        Price price2 = new Price();
        when(priceRepository.findAllWithProductAndStore()).thenReturn(List.of(price1, price2));

        // WHEN
        List<Price> result = priceService.getAllPrices();

        // THEN
        assertEquals(2, result.size());
        verify(priceRepository).findAllWithProductAndStore();
    }

    @Test
    void testGetPricesByProductId() {
        // GIVEN
        Long productId = 1L;
        Price price = new Price();
        Product product = new Product();
        product.setProductName("Test");
        product.setProductId(1L);

        Store store = new Store();
        store.setStoreId(1L);

        price.setPriceId(1L);
        price.setProduct(product);
        price.setStore(store);

        PriceDTO priceDTO = new PriceDTO();
        priceDTO.setPriceId(1L);
        priceDTO.setPrice(price.getPrice());
        when(priceRepository.findByProduct_ProductId(productId)).thenReturn(List.of(price));
        when(priceMapper.toDtoList(List.of(price))).thenReturn(List.of(priceDTO));

        // WHEN
        List<PriceDTO> result = priceService.getPricesByProductId(productId);

        // THEN
        assertEquals(1, result.size());
        assertEquals(price.getPriceId(), result.get(0).getPriceId());
        assertEquals(price.getPrice(), result.get(0).getPrice());
        verify(priceRepository).findByProduct_ProductId(productId);
    }


    @Test
    void testGetPriceHistoryByProductIdAndDataRange() {
        // GIVEN
        Long productId = 1L;
        Long storeId = 1L;
        LocalDateTime startDateTime = LocalDate.of(2025, 5, 29).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.of(2025, 6, 8)
                .atTime(23, 59, 59, 999999999);
        PriceHistory priceHistory = new PriceHistory();

        when(priceHistoryRepository.findByProductIdAndStoreIdAndDateRange(productId, storeId, startDateTime, endDateTime))
                .thenReturn(List.of(priceHistory));

        // WHEN
        List<PriceHistory> result = priceService.getPriceHistoryByProductIdAndDataRange(
                productId, storeId, startDateTime.toLocalDate(), endDateTime.toLocalDate());

        // THEN
        assertEquals(1, result.size());
        verify(priceHistoryRepository).findByProductIdAndStoreIdAndDateRange(productId, storeId, startDateTime, endDateTime);
    }

    @Test
    void testExportPricesToJson() {
        // GIVEN
        Price price = new Price();
        when(priceRepository.findAllWithProductAndStore()).thenReturn(List.of(price));

        // WHEN
        byte[] result = priceService.exportPricesToJson();

        // THEN
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testImportPricesFromJson() {
        // GIVEN
        String jsonData = "[{\"priceId\":1,\"price\":100,\"productId\":1,\"storeId\":1}]";
        byte[] data = jsonData.getBytes();
        Price price = new Price();
        Product product = new Product();
        product.setProductName("Test");
        product.setProductId(1L);

        Store store = new Store();
        store.setStoreId(1L);

        price.setPriceId(1L);
        price.setProduct(product);
        price.setStore(store);

        when(priceMapper.toEntityList(ArgumentMatchers.anyList())).thenReturn(List.of(price));
        when(priceRepository.saveAll(anyList())).thenReturn(List.of(new Price()));

        // WHEN
        List<PriceDTO> result = priceService.importPricesFromJson(data);

        // THEN
        assertEquals(1, result.size());
        assertEquals(price.getPriceId(), result.get(0).getPriceId());
        verify(priceRepository).saveAll(anyList());
    }
}
