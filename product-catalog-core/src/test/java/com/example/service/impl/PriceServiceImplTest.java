package com.example.service.impl;

import com.example.chart.ChartGenerator;
import com.example.dao.PriceDao;
import com.example.dao.PriceHistoryDao;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceServiceImplTest {

    @Mock
    private PriceDao priceDao;

    @Mock
    private PriceHistoryDao priceHistoryDao;

    @Mock
    private PriceMapper priceMapper;

    @InjectMocks
    private PriceServiceImpl priceService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        ChartGenerator chartGenerator = new ChartGenerator();
        priceService = new PriceServiceImpl(priceDao, priceHistoryDao, chartGenerator,
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
        when(priceDao.create(price)).thenReturn(price);

        // WHEN
        Price result = priceService.createPrice(price);

        // THEN
        assertEquals(price, result);
        verify(priceDao).create(price);
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

        when(priceDao.findById(1L)).thenReturn(price);

        // WHEN
        Price result = priceService.getPriceById(1L);

        // THEN
        assertEquals(price, result);
        verify(priceDao).findById(1L);
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

        when(priceDao.findById(currentPrice.getPriceId())).thenReturn(currentPrice);

        // WHEN
        priceService.updatePrice(newPrice);

        // THEN
        verify(priceHistoryDao).create(any(PriceHistory.class));
        verify(priceDao).update(newPrice);
    }

    @Test
    void testDeletePrice() {
        // GIVEN
        Price currentPrice = new Price();
        currentPrice.setPriceId(1L);
        when(priceDao.findById(currentPrice.getPriceId())).thenReturn(currentPrice);

        // WHEN
        priceService.deletePrice(1L);

        // THEN
        verify(priceDao).delete(1L);
    }

    @Test
    void testGetAllPrices() {
        // GIVEN
        Price price1 = new Price();
        Price price2 = new Price();
        when(priceDao.findAll()).thenReturn(List.of(price1, price2));

        // WHEN
        List<Price> result = priceService.getAllPrices();

        // THEN
        assertEquals(2, result.size());
        verify(priceDao).findAll();
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
        when(priceDao.findByProductId(productId)).thenReturn(List.of(price));
        when(priceMapper.toDtoList(List.of(price))).thenReturn(List.of(priceDTO));

        // WHEN
        List<PriceDTO> result = priceService.getPricesByProductId(productId);

        // THEN
        assertEquals(1, result.size());
        assertEquals(price.getPriceId(), result.get(0).getPriceId());
        assertEquals(price.getPrice(), result.get(0).getPrice());
        verify(priceDao).findByProductId(productId);
    }


    @Test
    void testGetPriceHistoryByProductIdAndDataRange() {
        // GIVEN
        Long productId = 1L;
        Long storeId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now();
        PriceHistory priceHistory = new PriceHistory();
        when(priceHistoryDao.findPriceHistoryByProductAndDateRange(productId, storeId, startDate, endDate))
                .thenReturn(List.of(priceHistory));

        // WHEN
        List<PriceHistory> result = priceService.getPriceHistoryByProductIdAndDataRange(productId, storeId, startDate, endDate);

        // THEN
        assertEquals(1, result.size());
        verify(priceHistoryDao).findPriceHistoryByProductAndDateRange(productId, storeId, startDate, endDate);
    }

    @Test
    void testExportPricesToJson() throws IOException {
        // GIVEN
        Price price = new Price();
        when(priceDao.findAll()).thenReturn(List.of(price));

        // WHEN
        byte[] result = priceService.exportPricesToJson();

        // THEN
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testImportPricesFromJson() throws IOException {
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

        when(priceMapper.toEntityList(anyList())).thenReturn(List.of(price));
        when(priceDao.create(any(Price.class))).thenReturn(new Price());

        // WHEN
        List<PriceDTO> result = priceService.importPricesFromJson(data);

        // THEN
        assertEquals(1, result.size());
        assertEquals(price.getPriceId(), result.get(0).getPriceId());
        verify(priceDao).create(any(Price.class));
    }
}
