package com.example.service.impl;

import com.example.dto.StoreDTO;
import com.example.entity.Store;
import com.example.mapper.StoreMapper;
import com.example.repository.StoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private StoreMapper storeMapper;

    @InjectMocks
    private StoreServiceImpl storeService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        storeService = new StoreServiceImpl(storeRepository, storeMapper, objectMapper);
    }

    @Test
    void testCreateStore() {
        // GIVEN
        Store store = new Store();
        when(storeRepository.save(store)).thenReturn(store);

        // WHEN
        Store result = storeService.createStore(store);

        // THEN
        assertEquals(store, result);
        verify(storeRepository).save(store);
    }

    @Test
    void testGetStoreById() {
        // GIVEN
        Long id = 1L;
        Store store = new Store();
        when(storeRepository.findById(id)).thenReturn(Optional.of(store));

        // WHEN
        Store result = storeService.getStoreById(id);

        // THEN
        assertEquals(store, result);
        verify(storeRepository).findById(id);
    }

    @Test
    void testUpdateStore() {
        // GIVEN
        Store store = new Store();
        store.setStoreId(1L);
        store.setStoreName("Test Store");
        when(storeRepository.findById(store.getStoreId())).thenReturn(Optional.of(store));

        // WHEN
        storeService.updateStore(store);

        // THEN
        verify(storeRepository).save(store);
    }

    @Test
    void testDeleteStore() {
        // GIVEN
        Long id = 1L;
        when(storeRepository.existsById(id)).thenReturn(true);

        // WHEN
        storeService.deleteStore(id);

        // THEN
        verify(storeRepository).deleteById(id);
    }

    @Test
    void testGetAllStores() {
        // GIVEN
        Store store1 = new Store();
        Store store2 = new Store();
        when(storeRepository.findAll()).thenReturn(List.of(store1, store2));

        // WHEN
        List<Store> result = storeService.getAllStores();

        // THEN
        assertEquals(2, result.size());
        verify(storeRepository).findAll();
    }

    @Test
    void testExportStoresToJson() {
        // GIVEN
        Store store = new Store();
        when(storeRepository.findAll()).thenReturn(List.of(store));

        // WHEN
        byte[] result = storeService.exportStoresToJson();

        // THEN
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testImportStoresFromJson() {
        // GIVEN
        String jsonData = "[{\"storeId\":1,\"storeName\":\"Test Store\"}]";
        byte[] data = jsonData.getBytes();
        Store store = new Store();
        store.setStoreId(1L);
        store.setStoreName("Test Store");

        when(storeMapper.toEntityList(anyList())).thenReturn(List.of(store));
        when(storeRepository.saveAll(anyList())).thenReturn(List.of(new Store()));

        // WHEN
        List<StoreDTO> result = storeService.importStoresFromJson(data);

        // THEN
        assertEquals(1, result.size());
        assertEquals(store.getStoreId(), result.get(0).getStoreId());
        verify(storeRepository).saveAll(anyList());
    }
}
