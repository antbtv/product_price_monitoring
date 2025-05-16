package com.example.controller;

import com.example.advice.GlobalExceptionHandler;
import com.example.dto.StoreCreateDTO;
import com.example.dto.StoreDTO;
import com.example.entity.Store;
import com.example.mapper.StoreMapper;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StoreControllerTest {

    @Mock
    private StoreService storeService;

    @Mock
    private StoreMapper storeMapper;

    @InjectMocks
    private StoreController storeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private final LocalDateTime testTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(storeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCreateStore() throws Exception {
        // GIVEN
        StoreCreateDTO createDTO = new StoreCreateDTO("Super Store", "123 Main St");
        Store store = new Store("Super Store", "123 Main St");
        store.setStoreId(1L);
        store.setCreatedAt(testTime);
        store.setUpdatedAt(testTime);

        StoreDTO storeDTO = new StoreDTO(1L, "Super Store", "123 Main St", testTime, testTime);

        when(storeService.createStore(any(Store.class))).thenReturn(store);
        when(storeMapper.toDto(store)).thenReturn(storeDTO);

        // WHEN
        mockMvc.perform(post("/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.storeId").value(1L))
                .andExpect(jsonPath("$.storeName").value("Super Store"))
                .andExpect(jsonPath("$.address").value("123 Main St"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        // THEN
        verify(storeService).createStore(any(Store.class));
        verify(storeMapper).toDto(store);
    }

    @Test
    void testGetStoreById() throws Exception {
        // GIVEN
        Store store = new Store("Mega Store", "456 Oak Ave");
        store.setStoreId(1L);
        store.setCreatedAt(testTime);

        StoreDTO storeDTO = new StoreDTO(1L, "Mega Store", "456 Oak Ave", testTime, testTime);

        when(storeService.getStoreById(1L)).thenReturn(store);
        when(storeMapper.toDto(store)).thenReturn(storeDTO);

        // WHEN
        mockMvc.perform(get("/stores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeId").value(1L))
                .andExpect(jsonPath("$.storeName").value("Mega Store"))
                .andExpect(jsonPath("$.address").value("456 Oak Ave"));

        // THEN
        verify(storeService).getStoreById(1L);
        verify(storeMapper).toDto(store);
    }

    @Test
    void testGetStoreById_NotFound() throws Exception {
        // GIVEN
        when(storeService.getStoreById(1L)).thenReturn(null);

        // WHEN
        mockMvc.perform(get("/stores/1"))
                .andExpect(status().isNotFound());

        // THEN
        verify(storeService).getStoreById(1L);
        verifyNoInteractions(storeMapper);
    }

    @Test
    void testUpdateStore() throws Exception {
        // GIVEN
        Store store = new Store("Updated Store", "789 Pine Rd");
        store.setStoreId(1L);
        store.setCreatedAt(testTime);
        store.setUpdatedAt(testTime.plusHours(1));

        StoreDTO storeDTO = new StoreDTO(1L, "Updated Store", "789 Pine Rd", testTime, testTime.plusHours(1));

        doNothing().when(storeService).updateStore(any(Store.class));
        when(storeMapper.toDto(any(Store.class))).thenReturn(storeDTO);

        // WHEN
        mockMvc.perform(put("/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(store)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeName").value("Updated Store"))
                .andExpect(jsonPath("$.address").value("789 Pine Rd"));

        // THEN
        verify(storeService).updateStore(any(Store.class));
        verify(storeMapper).toDto(any(Store.class));
    }

    @Test
    void testPartialUpdateStore() throws Exception {
        // GIVEN
        StoreCreateDTO updateDTO = new StoreCreateDTO("New Name", null);
        Store existingStore = new Store("Old Name", "123 Main St");
        existingStore.setStoreId(1L);

        StoreDTO storeDTO = new StoreDTO(1L, "New Name", "123 Main St", testTime, testTime.plusHours(1));

        when(storeService.getStoreById(1L)).thenReturn(existingStore);
        doNothing().when(storeService).updateStore(any(Store.class));
        when(storeMapper.toDto(any(Store.class))).thenReturn(storeDTO);

        // WHEN
        mockMvc.perform(patch("/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeName").value("New Name"))
                .andExpect(jsonPath("$.address").value("123 Main St"));

        // THEN
        verify(storeService).getStoreById(1L);
        verify(storeService).updateStore(any(Store.class));
        verify(storeMapper).toDto(any(Store.class));
    }

    @Test
    void testPartialUpdateStore_WithException() throws Exception {
        // GIVEN
        StoreCreateDTO updateDTO = new StoreCreateDTO("New Name", null);
        Store existingStore = new Store("Old Name", "123 Main St");
        existingStore.setStoreId(1L);

        when(storeService.getStoreById(1L)).thenReturn(existingStore);
        doThrow(new RuntimeException("Test exception")).when(storeService).updateStore(any(Store.class));

        // WHEN
        mockMvc.perform(patch("/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isInternalServerError());

        // THEN
        verify(storeService).getStoreById(1L);
        verify(storeService).updateStore(any(Store.class));
        verifyNoInteractions(storeMapper);
    }

    @Test
    void testDeleteStore() throws Exception {
        // WHEN
        mockMvc.perform(delete("/stores/1"))
                .andExpect(status().isNoContent());

        // THEN
        verify(storeService).deleteStore(1L);
    }

    @Test
    void testGetAllStores() throws Exception {
        // GIVEN
        Store store1 = new Store("Store 1", "Address 1");
        store1.setStoreId(1L);
        Store store2 = new Store("Store 2", "Address 2");
        store2.setStoreId(2L);

        List<Store> stores = List.of(store1, store2);
        List<StoreDTO> storeDTOs = List.of(
                new StoreDTO(1L, "Store 1", "Address 1", testTime, testTime),
                new StoreDTO(2L, "Store 2", "Address 2", testTime, testTime)
        );

        when(storeService.getAllStores()).thenReturn(stores);
        when(storeMapper.toDtoList(stores)).thenReturn(storeDTOs);

        // WHEN
        mockMvc.perform(get("/stores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].storeName").value("Store 1"))
                .andExpect(jsonPath("$[1].storeName").value("Store 2"));

        // THEN
        verify(storeService).getAllStores();
        verify(storeMapper).toDtoList(stores);
    }

    @Test
    void testExportStores() throws Exception {
        // GIVEN
        byte[] mockData = "{\"stores\":[]}".getBytes();
        when(storeService.exportStoresToJson()).thenReturn(mockData);

        // WHEN
        mockMvc.perform(get("/stores/export"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"stores.json\""))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().bytes(mockData));

        // THEN
        verify(storeService).exportStoresToJson();
    }

    @Test
    void testImportStores() throws Exception {
        // GIVEN
        byte[] jsonData = "[{\"storeName\":\"Imported Store\"}]".getBytes();
        StoreDTO importedStore = new StoreDTO(1L, "Imported Store", "Address", testTime, testTime);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "stores.json",
                MediaType.APPLICATION_JSON_VALUE,
                jsonData);

        when(storeService.importStoresFromJson(jsonData))
                .thenReturn(List.of(importedStore));

        // WHEN
        mockMvc.perform(multipart("/stores/import")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].storeName").value("Imported Store"));

        // THEN
        verify(storeService).importStoresFromJson(jsonData);
    }

    @Test
    void testImportStores_EmptyFile() throws Exception {
        // GIVEN
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.json",
                MediaType.APPLICATION_JSON_VALUE,
                new byte[0]);

        // WHEN
        mockMvc.perform(multipart("/stores/import")
                        .file(emptyFile))
                .andExpect(status().isBadRequest());

        // THEN
        verifyNoInteractions(storeService);
    }
}