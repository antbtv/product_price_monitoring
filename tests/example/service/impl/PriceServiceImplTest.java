package com.example.service.impl;

import com.example.utils.ChartGenerator;
import com.example.repository.PriceDao;
import com.example.repository.PriceHistoryDao;
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
        Mockito.when(priceDao.create(price)).thenReturn(price);

        // WHEN
        Price result = priceService.createPrice(price);

        // THEN
        Assertions.assertEquals(price, result);
        Mockito.verify(priceDao).create(price);
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

        Mockito.when(priceDao.findById(1L)).thenReturn(price);

        // WHEN
        Price result = priceService.getPriceById(1L);

        // THEN
        Assertions.assertEquals(price, result);
        Mockito.verify(priceDao).findById(1L);
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

        Mockito.when(priceDao.findById(currentPrice.getPriceId())).thenReturn(currentPrice);

        // WHEN
        priceService.updatePrice(newPrice);

        // THEN
        Mockito.verify(priceHistoryDao).create(ArgumentMatchers.any(PriceHistory.class));
        Mockito.verify(priceDao).update(newPrice);
    }

    @Test
    void testDeletePrice() {
        // GIVEN
        Price currentPrice = new Price();
        currentPrice.setPriceId(1L);
        Mockito.when(priceDao.findById(currentPrice.getPriceId())).thenReturn(currentPrice);

        // WHEN
        priceService.deletePrice(1L);

        // THEN
        Mockito.verify(priceDao).delete(1L);
    }

    @Test
    void testGetAllPrices() {
        // GIVEN
        Price price1 = new Price();
        Price price2 = new Price();
        Mockito.when(priceDao.findAll()).thenReturn(List.of(price1, price2));

        // WHEN
        List<Price> result = priceService.getAllPrices();

        // THEN
        Assertions.assertEquals(2, result.size());
        Mockito.verify(priceDao).findAll();
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
        Mockito.when(priceDao.findByProductId(productId)).thenReturn(List.of(price));
        Mockito.when(priceMapper.toDtoList(List.of(price))).thenReturn(List.of(priceDTO));

        // WHEN
        List<PriceDTO> result = priceService.getPricesByProductId(productId);

        // THEN
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(price.getPriceId(), result.get(0).getPriceId());
        Assertions.assertEquals(price.getPrice(), result.get(0).getPrice());
        Mockito.verify(priceDao).findByProductId(productId);
    }


    @Test
    void testGetPriceHistoryByProductIdAndDataRange() {
        // GIVEN
        Long productId = 1L;
        Long storeId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now();
        PriceHistory priceHistory = new PriceHistory();
        Mockito.when(priceHistoryDao.findPriceHistoryByProductAndDateRange(productId, storeId, startDate, endDate))
                .thenReturn(List.of(priceHistory));

        // WHEN
        List<PriceHistory> result = priceService.getPriceHistoryByProductIdAndDataRange(productId, storeId, startDate, endDate);

        // THEN
        Assertions.assertEquals(1, result.size());
        Mockito.verify(priceHistoryDao).findPriceHistoryByProductAndDateRange(productId, storeId, startDate, endDate);
    }

    @Test
    void testExportPricesToJson() throws IOException {
        // GIVEN
        Price price = new Price();
        Mockito.when(priceDao.findAll()).thenReturn(List.of(price));

        // WHEN
        byte[] result = priceService.exportPricesToJson();

        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.length > 0);
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

        Mockito.when(priceMapper.toEntityList(ArgumentMatchers.anyList())).thenReturn(List.of(price));
        Mockito.when(priceDao.create(ArgumentMatchers.any(Price.class))).thenReturn(new Price());

        // WHEN
        List<PriceDTO> result = priceService.importPricesFromJson(data);

        // THEN
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(price.getPriceId(), result.get(0).getPriceId());
        Mockito.verify(priceDao).create(ArgumentMatchers.any(Price.class));
    }
}
