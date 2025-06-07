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
import com.example.service.DataLogService;
import com.example.service.PriceService;
import com.example.service.ProductService;
import com.example.service.StoreService;
import com.example.service.security.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
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
    private UserService userService;

    @Mock
    private DataLogService dataLogService;

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

        Mockito.when(productService.getProductById(1L)).thenReturn(product);
        Mockito.when(storeService.getStoreById(1L)).thenReturn(store);
        Mockito.when(priceService.createPrice(ArgumentMatchers.any(Price.class))).thenReturn(price);
        Mockito.when(priceMapper.toDto(price)).thenReturn(priceDTO);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.priceId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.storeId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("$.recordedAt").exists());

        // THEN
        Mockito.verify(productService).getProductById(1L);
        Mockito.verify(storeService).getStoreById(1L);
        Mockito.verify(priceService).createPrice(ArgumentMatchers.any(Price.class));
        Mockito.verify(priceMapper).toDto(price);
    }

    @Test
    void testGetPriceById() throws Exception {
        // GIVEN
        Price price = new Price(new Product(), new Store(), 100);
        price.setPriceId(1L);
        price.setRecordedAt(testTime);

        PriceDTO priceDTO = new PriceDTO(1L, 1L, 1L, 100, testTime);

        Mockito.when(priceService.getPriceById(1L)).thenReturn(price);
        Mockito.when(priceMapper.toDto(price)).thenReturn(priceDTO);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/prices/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.priceId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("$.recordedAt").exists());

        // THEN
        Mockito.verify(priceService).getPriceById(1L);
        Mockito.verify(priceMapper).toDto(price);
    }

    @Test
    void testGetPriceById_NotFound() throws Exception {
        // GIVEN
        Mockito.when(priceService.getPriceById(1L)).thenReturn(null);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/prices/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        // THEN
        Mockito.verify(priceService).getPriceById(1L);
        Mockito.verifyNoInteractions(priceMapper);
    }

    @Test
    void testUpdatePrice() throws Exception {
        // GIVEN
        Long priceId = 1L;
        PriceDTO requestDTO = new PriceDTO();
        requestDTO.setPriceId(priceId);
        requestDTO.setProductId(1L);
        requestDTO.setStoreId(1L);
        requestDTO.setPrice(150);
        requestDTO.setRecordedAt(testTime);

        Price priceEntity = new Price();
        priceEntity.setPriceId(priceId);
        priceEntity.setPrice(150);
        priceEntity.setRecordedAt(testTime);

        Mockito.when(priceMapper.toEntity(ArgumentMatchers.any(PriceDTO.class))).thenReturn(priceEntity);
        Mockito.doNothing().when(priceService).updatePrice(ArgumentMatchers.any(Price.class));

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.put("/prices/{id}", priceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.priceId").value(priceId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(150))
                .andExpect(MockMvcResultMatchers.jsonPath("$.recordedAt").exists());

        // THEN
        Mockito.verify(priceMapper).toEntity(ArgumentMatchers.argThat(dto ->
                dto.getPriceId().equals(priceId) &&
                        dto.getPrice() == 150
        ));
        Mockito.verify(priceService).updatePrice(ArgumentMatchers.any(Price.class));
    }


    @Test
    void testPartialUpdatePrice() throws Exception {
        // GIVEN
        PriceCreateDTO updateDTO = new PriceCreateDTO(null, null, 200);
        Price existingPrice = new Price(new Product(), new Store(), 100);
        existingPrice.setPriceId(1L);
        existingPrice.setRecordedAt(testTime);

        PriceDTO priceDTO = new PriceDTO(1L, 1L, 1L, 200, testTime);

        Mockito.when(priceService.getPriceById(1L)).thenReturn(existingPrice);
        Mockito.doNothing().when(priceService).updatePrice(ArgumentMatchers.any(Price.class));
        Mockito.when(priceMapper.toDto(existingPrice)).thenReturn(priceDTO);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.patch("/prices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(200));

        // THEN
        Mockito.verify(priceService).getPriceById(1L);
        Mockito.verify(priceService).updatePrice(ArgumentMatchers.any(Price.class));
        Mockito.verify(priceMapper).toDto(existingPrice);
    }

    @Test
    void testDeletePrice() throws Exception {
        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/prices/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // THEN
        Mockito.verify(priceService).deletePrice(1L);
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

        Mockito.when(priceService.getAllPrices()).thenReturn(prices);
        Mockito.when(priceMapper.toDtoList(prices)).thenReturn(priceDTOs);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/prices"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].price").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].price").value(200));

        // THEN
        Mockito.verify(priceService).getAllPrices();
        Mockito.verify(priceMapper).toDtoList(prices);
    }

    @Test
    void testComparePrices() throws Exception {
        // GIVEN
        List<PriceDTO> priceDTOs = List.of(
                new PriceDTO(1L, 1L, 1L, 100, testTime),
                new PriceDTO(2L, 1L, 2L, 150, testTime)
        );

        Mockito.when(priceService.getPricesByProductId(1L)).thenReturn(priceDTOs);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/prices/compare/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].price").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].price").value(150));

        // THEN
        Mockito.verify(priceService).getPricesByProductId(1L);
    }

    @Test
    void testComparePrices_NotFound() throws Exception {
        // GIVEN
        Mockito.when(priceService.getPricesByProductId(1L)).thenReturn(List.of());

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/prices/compare/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        // THEN
        Mockito.verify(priceService).getPricesByProductId(1L);
    }

    @Test
    void testGetPriceHistory_Success() throws Exception {
        // GIVEN
        Long productId = 1L;
        HistoryRequestDTO request = new HistoryRequestDTO(
                2L,
                LocalDate.of(2024, 1, 1),
                LocalDate.now()
        );

        PriceHistory history = new PriceHistory();
        history.setPrice(100);
        history.setRecordedAt(LocalDateTime.now());

        PriceHistoryDTO historyDTO = new PriceHistoryDTO(1L, 1L, 1L,
                100, LocalDateTime.now());

        Mockito.when(priceService.getPriceHistoryByProductIdAndDataRange(
                ArgumentMatchers.eq(productId),
                ArgumentMatchers.eq(request.getStoreId()),
                ArgumentMatchers.eq(request.getStartDate()),
                ArgumentMatchers.eq(request.getEndDate())
        )).thenReturn(List.of(history));

        Mockito.when(priceHistoryMapper.toDto(history)).thenReturn(historyDTO);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/prices/history/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].price").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].recordedAt").exists());

        // THEN
        Mockito.verify(priceService).getPriceHistoryByProductIdAndDataRange(
                productId,
                request.getStoreId(),
                request.getStartDate(),
                request.getEndDate()
        );
    }


    @Test
    void testGetPriceHistoryChart_Success() throws Exception {
        // GIVEN
        Long productId = 1L;
        HistoryRequestDTO request = new HistoryRequestDTO(
                3L,
                LocalDate.of(2024, 1, 1),
                LocalDate.now()
        );

        byte[] mockChart = new byte[]{0x00, 0x01, 0x02}; // Пример данных изображения

        Mockito.when(priceService.generatePriceHistoryChart(
                ArgumentMatchers.eq(productId),
                ArgumentMatchers.eq(request.getStoreId()),
                ArgumentMatchers.eq(request.getStartDate()),
                ArgumentMatchers.eq(request.getEndDate())
        )).thenReturn(mockChart);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/prices/history/chart/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=price_chart.png"))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.IMAGE_PNG))
                .andExpect(MockMvcResultMatchers.content().bytes(mockChart));
    }

    @Test
    void testExportPrices() throws Exception {
        // GIVEN
        byte[] mockData = "{\"prices\":[]}".getBytes();
        Mockito.when(priceService.exportPricesToJson()).thenReturn(mockData);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/prices/export"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"prices.json\""))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().bytes(mockData));

        // THEN
        Mockito.verify(priceService).exportPricesToJson();
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

        Mockito.when(priceService.importPricesFromJson(jsonData))
                .thenReturn(List.of(importedPrice));

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.multipart("/prices/import")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].price").value(100));

        // THEN
        Mockito.verify(priceService).importPricesFromJson(jsonData);
    }

}