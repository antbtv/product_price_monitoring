package com.example.controller;

import com.example.dto.HistoryRequestDTO;
import com.example.dto.PriceCreateDTO;
import com.example.dto.PriceDTO;
import com.example.dto.PriceHistoryDTO;
import com.example.entity.Price;
import com.example.entity.PriceHistory;
import com.example.entity.Product;
import com.example.entity.Store;
import com.example.mapper.PriceHistoryMapper;
import com.example.mapper.PriceMapper;
import com.example.service.PriceService;
import com.example.service.ProductService;
import com.example.service.StoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PriceControllerTest {

    @Mock
    private PriceService priceService;

    @Mock
    private ProductService productService;

    @Mock
    private StoreService storeService;

    @Mock
    private PriceMapper priceMapper;

    @Mock
    private PriceHistoryMapper priceHistoryMapper;

    @InjectMocks
    private PriceController priceController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private final LocalDateTime testTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(priceController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCreatePrice() throws Exception {
        // GIVEN
        PriceCreateDTO createDTO = new PriceCreateDTO(1L, 1L, 100);
        Product product = new Product("Product", null);
        product.setProductId(1L);
        Store store = new Store("Store", "Address");
        store.setStoreId(1L);

        Price price = new Price(product, store, 100);
        price.setPriceId(1L);
        price.setRecordedAt(testTime);

        PriceDTO priceDTO = new PriceDTO(1L, 1L, 1L, 100, testTime);

        when(productService.getProductById(1L)).thenReturn(product);
        when(storeService.getStoreById(1L)).thenReturn(store);
        when(priceService.createPrice(any(Price.class))).thenReturn(price);
        when(priceMapper.toDto(price)).thenReturn(priceDTO);

        // WHEN
        mockMvc.perform(post("/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.priceId").value(1L))
                .andExpect(jsonPath("$.productId").value(1L))
                .andExpect(jsonPath("$.storeId").value(1L))
                .andExpect(jsonPath("$.price").value(100))
                .andExpect(jsonPath("$.recordedAt").exists());

        // THEN
        verify(productService).getProductById(1L);
        verify(storeService).getStoreById(1L);
        verify(priceService).createPrice(any(Price.class));
        verify(priceMapper).toDto(price);
    }

    @Test
    void testGetPriceById() throws Exception {
        // GIVEN
        Price price = new Price(new Product(), new Store(), 100);
        price.setPriceId(1L);
        price.setRecordedAt(testTime);

        PriceDTO priceDTO = new PriceDTO(1L, 1L, 1L, 100, testTime);

        when(priceService.getPriceById(1L)).thenReturn(price);
        when(priceMapper.toDto(price)).thenReturn(priceDTO);

        // WHEN
        mockMvc.perform(get("/prices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceId").value(1L))
                .andExpect(jsonPath("$.price").value(100))
                .andExpect(jsonPath("$.recordedAt").exists());

        // THEN
        verify(priceService).getPriceById(1L);
        verify(priceMapper).toDto(price);
    }

    @Test
    void testGetPriceById_NotFound() throws Exception {
        // GIVEN
        when(priceService.getPriceById(1L)).thenReturn(null);

        // WHEN
        mockMvc.perform(get("/prices/1"))
                .andExpect(status().isNotFound());

        // THEN
        verify(priceService).getPriceById(1L);
        verifyNoInteractions(priceMapper);
    }

    @Test
    void testUpdatePrice() throws Exception {
        // GIVEN
        Price price = new Price(new Product(), new Store(), 150);
        price.setPriceId(1L);
        price.setRecordedAt(testTime);

        PriceDTO priceDTO = new PriceDTO(1L, 1L, 1L, 150, testTime);

        doNothing().when(priceService).updatePrice(any(Price.class));
        when(priceMapper.toDto(any(Price.class))).thenReturn(priceDTO);

        // WHEN
        mockMvc.perform(put("/prices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(price)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceId").value(1L))
                .andExpect(jsonPath("$.price").value(150))
                .andExpect(jsonPath("$.recordedAt").exists());

        // THEN
        verify(priceService).updatePrice(any(Price.class));
        verify(priceMapper).toDto(any(Price.class));
    }

    @Test
    void testPartialUpdatePrice() throws Exception {
        // GIVEN
        PriceCreateDTO updateDTO = new PriceCreateDTO(null, null, 200);
        Price existingPrice = new Price(new Product(), new Store(), 100);
        existingPrice.setPriceId(1L);
        existingPrice.setRecordedAt(testTime);

        PriceDTO priceDTO = new PriceDTO(1L, 1L, 1L, 200, testTime);

        when(priceService.getPriceById(1L)).thenReturn(existingPrice);
        doNothing().when(priceService).updatePrice(any(Price.class));
        when(priceMapper.toDto(existingPrice)).thenReturn(priceDTO);

        // WHEN
        mockMvc.perform(patch("/prices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(200));

        // THEN
        verify(priceService).getPriceById(1L);
        verify(priceService).updatePrice(any(Price.class));
        verify(priceMapper).toDto(existingPrice);
    }

    @Test
    void testDeletePrice() throws Exception {
        // WHEN
        mockMvc.perform(delete("/prices/1"))
                .andExpect(status().isNoContent());

        // THEN
        verify(priceService).deletePrice(1L);
    }

    @Test
    void testGetAllPrices() throws Exception {
        // GIVEN
        Price price1 = new Price(new Product(), new Store(), 100);
        price1.setPriceId(1L);
        Price price2 = new Price(new Product(), new Store(), 200);
        price2.setPriceId(2L);

        List<Price> prices = List.of(price1, price2);
        List<PriceDTO> priceDTOs = List.of(
                new PriceDTO(1L, 1L, 1L, 100, testTime),
                new PriceDTO(2L, 2L, 2L, 200, testTime)
        );

        when(priceService.getAllPrices()).thenReturn(prices);
        when(priceMapper.toDtoList(prices)).thenReturn(priceDTOs);

        // WHEN
        mockMvc.perform(get("/prices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price").value(100))
                .andExpect(jsonPath("$[1].price").value(200));

        // THEN
        verify(priceService).getAllPrices();
        verify(priceMapper).toDtoList(prices);
    }

    @Test
    void testComparePrices() throws Exception {
        // GIVEN
        List<PriceDTO> priceDTOs = List.of(
                new PriceDTO(1L, 1L, 1L, 100, testTime),
                new PriceDTO(2L, 1L, 2L, 150, testTime)
        );

        when(priceService.getPricesByProductId(1L)).thenReturn(priceDTOs);

        // WHEN
        mockMvc.perform(get("/prices/compare/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price").value(100))
                .andExpect(jsonPath("$[1].price").value(150));

        // THEN
        verify(priceService).getPricesByProductId(1L);
    }

    @Test
    void testComparePrices_NotFound() throws Exception {
        // GIVEN
        when(priceService.getPricesByProductId(1L)).thenReturn(List.of());

        // WHEN
        mockMvc.perform(get("/prices/compare/1"))
                .andExpect(status().isNotFound());

        // THEN
        verify(priceService).getPricesByProductId(1L);
    }

    @Test
    void testGetPriceHistory() throws Exception {
        // GIVEN
        HistoryRequestDTO request = new HistoryRequestDTO(
                1L, testTime.toLocalDate(), testTime.toLocalDate());

        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setPrice(100);
        priceHistory.setRecordedAt(testTime);

        PriceHistoryDTO priceHistoryDTO = new PriceHistoryDTO(1L, 1L, 1L, 100, testTime);

        when(priceService.getPriceHistoryByProductIdAndDataRange(
                eq(1L), eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(priceHistory));
        when(priceHistoryMapper.toDto(priceHistory)).thenReturn(priceHistoryDTO);

        // WHEN
        mockMvc.perform(put("/prices/history/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").value(100))
                .andExpect(jsonPath("$[0].recordedAt").exists());

        // THEN
        verify(priceService).getPriceHistoryByProductIdAndDataRange(
                eq(1L), eq(1L), any(LocalDate.class), any(LocalDate.class));
        verify(priceHistoryMapper).toDto(priceHistory);
    }

    @Test
    void testExportPrices() throws Exception {
        // GIVEN
        byte[] mockData = "{\"prices\":[]}".getBytes();
        when(priceService.exportPricesToJson()).thenReturn(mockData);

        // WHEN
        mockMvc.perform(get("/prices/export"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"prices.json\""))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().bytes(mockData));

        // THEN
        verify(priceService).exportPricesToJson();
    }

    @Test
    void testImportPrices() throws Exception {
        // GIVEN
        byte[] jsonData = "[{\"price\":100}]".getBytes();
        PriceDTO importedPrice = new PriceDTO(1L, 1L, 1L, 100, testTime);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "prices.json",
                MediaType.APPLICATION_JSON_VALUE,
                jsonData);

        when(priceService.importPricesFromJson(jsonData))
                .thenReturn(List.of(importedPrice));

        // WHEN
        mockMvc.perform(multipart("/prices/import")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").value(100));

        // THEN
        verify(priceService).importPricesFromJson(jsonData);
    }

//    @Test
//    void testGetPriceHistoryChart() throws Exception {
//        // GIVEN
//        HistoryRequestDTO request = new HistoryRequestDTO(
//                1L, testTime.toLocalDate(), testTime.toLocalDate());
//
//        PriceHistory priceHistory = new PriceHistory();
//        priceHistory.setPrice(100);
//        priceHistory.setRecordedAt(testTime);
//
//        byte[] chartBytes = new byte[]{1, 2, 3};
//
//        when(priceService.getPriceHistoryByProductIdAndDataRange(
//                eq(1L), eq(1L), any(LocalDate.class), any(LocalDate.class)))
//                .thenReturn(List.of(priceHistory));
//        when(priceService.getPriceHistoryByProductIdAndDataRange(anyList())).thenReturn(chartBytes);
//
//        // WHEN
//        mockMvc.perform(put("/prices/history/chart/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(header().string("Content-Disposition",
//                        "attachment; filename=price_chart.png"))
//                .andExpect(content().contentType(MediaType.IMAGE_PNG))
//                .andExpect(content().bytes(chartBytes));
//
//        // THEN
//        verify(priceService).getPriceHistoryByProductIdAndDataRange(
//                eq(1L), eq(1L), any(LocalDate.class), any(LocalDate.class));
//        verify(priceService).getPriceHistoryByProductIdAndDataRange(anyList());
//    }
}