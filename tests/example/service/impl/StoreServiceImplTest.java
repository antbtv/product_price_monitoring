package com.example.service.impl;

import com.example.repository.StoreDao;
import com.example.dto.StoreDTO;
import com.example.entity.Store;
import com.example.mapper.StoreMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

    @Mock
    private StoreDao storeDao;

    @Mock
    private StoreMapper storeMapper;

    @InjectMocks
    private StoreServiceImpl storeService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        storeService = new StoreServiceImpl(storeDao, storeMapper, objectMapper);
    }

    @Test
    void testCreateStore() {
        // GIVEN
        Store store = new Store();
        Mockito.when(storeDao.create(store)).thenReturn(store);

        // WHEN
        Store result = storeService.createStore(store);

        // THEN
        Assertions.assertEquals(store, result);
        Mockito.verify(storeDao).create(store);
    }

    @Test
    void testGetStoreById() {
        // GIVEN
        Long id = 1L;
        Store store = new Store();
        Mockito.when(storeDao.findById(id)).thenReturn(store);

        // WHEN
        Store result = storeService.getStoreById(id);

        // THEN
        Assertions.assertEquals(store, result);
        Mockito.verify(storeDao).findById(id);
    }

    @Test
    void testUpdateStore() {
        // GIVEN
        Store store = new Store();
        store.setStoreId(1L);
        store.setStoreName("Test Store");
        Mockito.when(storeDao.findById(store.getStoreId())).thenReturn(store);

        // WHEN
        storeService.updateStore(store);

        // THEN
        Mockito.verify(storeDao).update(store);
    }

    @Test
    void testDeleteStore() {
        // GIVEN
        Store store = new Store();
        store.setStoreId(1L);
        store.setStoreName("Test Store");
        Mockito.when(storeDao.findById(store.getStoreId())).thenReturn(store);

        // WHEN
        storeService.deleteStore(1L);

        // THEN
        Mockito.verify(storeDao).delete(1L);
    }

    @Test
    void testGetAllStores() {
        // GIVEN
        Store store1 = new Store();
        Store store2 = new Store();
        Mockito.when(storeDao.findAll()).thenReturn(List.of(store1, store2));

        // WHEN
        List<Store> result = storeService.getAllStores();

        // THEN
        Assertions.assertEquals(2, result.size());
        Mockito.verify(storeDao).findAll();
    }

    @Test
    void testExportStoresToJson() {
        // GIVEN
        Store store = new Store();
        Mockito.when(storeDao.findAll()).thenReturn(List.of(store));

        // WHEN
        byte[] result = storeService.exportStoresToJson();

        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.length > 0);
    }

    @Test
    void testImportStoresFromJson() {
        // GIVEN
        String jsonData = "[{\"storeId\":1,\"storeName\":\"Test Store\"}]";
        byte[] data = jsonData.getBytes();
        Store store = new Store();
        store.setStoreId(1L);
        store.setStoreName("Test Store");

        Mockito.when(storeMapper.toEntityList(ArgumentMatchers.anyList())).thenReturn(List.of(store));
        Mockito.when(storeDao.create(ArgumentMatchers.any(Store.class))).thenReturn(new Store());

        // WHEN
        List<StoreDTO> result = storeService.importStoresFromJson(data);

        // THEN
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(store.getStoreId(), result.get(0).getStoreId());
        Mockito.verify(storeDao).create(ArgumentMatchers.any(Store.class));
    }
}
