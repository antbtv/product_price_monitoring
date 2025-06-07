package com.example.controller;

import com.example.advice.GlobalExceptionHandler;
import com.example.dto.StoreCreateDTO;
import com.example.dto.StoreDTO;
import com.example.entity.Store;
import com.example.mapper.StoreMapper;
import com.example.service.DataLogService;
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
    private UserService userService;

    @Mock
    private DataLogService dataLogService;

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

        Mockito.when(storeService.createStore(ArgumentMatchers.any(Store.class))).thenReturn(store);
        Mockito.when(storeMapper.toDto(store)).thenReturn(storeDTO);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.storeId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.storeName").value("Super Store"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("123 Main St"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").exists());

        // THEN
        Mockito.verify(storeService).createStore(ArgumentMatchers.any(Store.class));
        Mockito.verify(storeMapper).toDto(store);
    }

    @Test
    void testGetStoreById() throws Exception {
        // GIVEN
        Store store = new Store("Mega Store", "456 Oak Ave");
        store.setStoreId(1L);
        store.setCreatedAt(testTime);

        StoreDTO storeDTO = new StoreDTO(1L, "Mega Store", "456 Oak Ave", testTime, testTime);

        Mockito.when(storeService.getStoreById(1L)).thenReturn(store);
        Mockito.when(storeMapper.toDto(store)).thenReturn(storeDTO);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/stores/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.storeId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.storeName").value("Mega Store"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("456 Oak Ave"));

        // THEN
        Mockito.verify(storeService).getStoreById(1L);
        Mockito.verify(storeMapper).toDto(store);
    }

    @Test
    void testGetStoreById_NotFound() throws Exception {
        // GIVEN
        Mockito.when(storeService.getStoreById(1L)).thenReturn(null);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/stores/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        // THEN
        Mockito.verify(storeService).getStoreById(1L);
        Mockito.verifyNoInteractions(storeMapper);
    }

    @Test
    void testUpdateStore() throws Exception {
        // GIVEN
        Long storeId = 1L;
        StoreDTO requestDTO = new StoreDTO();
        requestDTO.setStoreId(storeId);
        requestDTO.setStoreName("Updated Store");
        requestDTO.setAddress("Pushkin's St");
        requestDTO.setCreatedAt(testTime);
        requestDTO.setUpdatedAt(testTime.plusHours(1));

        Store storeEntity = new Store("Updated Store", "Pushkin's St");
        storeEntity.setStoreId(storeId);
        storeEntity.setCreatedAt(testTime);
        storeEntity.setUpdatedAt(testTime.plusHours(1));

        Mockito.when(storeMapper.toEntity(ArgumentMatchers.any(StoreDTO.class))).thenReturn(storeEntity);
        Mockito.doNothing().when(storeService).updateStore(ArgumentMatchers.any(Store.class));

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.put("/stores/{id}", storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.storeName").value("Updated Store"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("Pushkin's St"));

        // THEN
        Mockito.verify(storeMapper).toEntity(ArgumentMatchers.argThat(dto ->
                dto.getStoreId().equals(storeId) &&
                        dto.getStoreName().equals("Updated Store") &&
                        dto.getAddress().equals("Pushkin's St")
        ));
        Mockito.verify(storeService).updateStore(ArgumentMatchers.any(Store.class));
    }

    @Test
    void testPartialUpdateStore() throws Exception {
        // GIVEN
        StoreCreateDTO updateDTO = new StoreCreateDTO("New Name", null);
        Store existingStore = new Store("Old Name", "123 Main St");
        existingStore.setStoreId(1L);

        StoreDTO storeDTO = new StoreDTO(1L, "New Name", "123 Main St", testTime, testTime.plusHours(1));

        Mockito.when(storeService.getStoreById(1L)).thenReturn(existingStore);
        Mockito.doNothing().when(storeService).updateStore(ArgumentMatchers.any(Store.class));
        Mockito.when(storeMapper.toDto(ArgumentMatchers.any(Store.class))).thenReturn(storeDTO);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.patch("/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.storeName").value("New Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("123 Main St"));

        // THEN
        Mockito.verify(storeService).getStoreById(1L);
        Mockito.verify(storeService).updateStore(ArgumentMatchers.any(Store.class));
        Mockito.verify(storeMapper).toDto(ArgumentMatchers.any(Store.class));
    }

    @Test
    void testDeleteStore() throws Exception {
        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/stores/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // THEN
        Mockito.verify(storeService).deleteStore(1L);
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

        Mockito.when(storeService.getAllStores()).thenReturn(stores);
        Mockito.when(storeMapper.toDtoList(stores)).thenReturn(storeDTOs);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/stores"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].storeName").value("Store 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].storeName").value("Store 2"));

        // THEN
        Mockito.verify(storeService).getAllStores();
        Mockito.verify(storeMapper).toDtoList(stores);
    }

    @Test
    void testExportStores() throws Exception {
        // GIVEN
        byte[] mockData = "{\"stores\":[]}".getBytes();
        Mockito.when(storeService.exportStoresToJson()).thenReturn(mockData);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/stores/export"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"stores.json\""))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().bytes(mockData));

        // THEN
        Mockito.verify(storeService).exportStoresToJson();
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

        Mockito.when(storeService.importStoresFromJson(jsonData))
                .thenReturn(List.of(importedStore));

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.multipart("/stores/import")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].storeName").value("Imported Store"));

        // THEN
        Mockito.verify(storeService).importStoresFromJson(jsonData);
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
        mockMvc.perform(MockMvcRequestBuilders.multipart("/stores/import")
                        .file(emptyFile))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // THEN
        Mockito.verifyNoInteractions(storeService);
    }
}