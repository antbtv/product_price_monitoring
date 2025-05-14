package com.example.service.impl;

import com.example.dao.StoreDao;
import com.example.dto.StoreDTO;
import com.example.entity.Store;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

    @Mock
    private StoreDao storeDao;

    @InjectMocks
    private StoreServiceImpl storeService;

    @Test
    void testCreateStore() {
        // GIVEN
        Store store = new Store();
        when(storeDao.create(store)).thenReturn(store);

        // WHEN
        Store result = storeService.createStore(store);

        // THEN
        assertEquals(store, result);
        verify(storeDao).create(store);
    }

    @Test
    void testGetStoreById() {
        // GIVEN
        Long id = 1L;
        Store store = new Store();
        when(storeDao.findById(id)).thenReturn(store);

        // WHEN
        Store result = storeService.getStoreById(id);

        // THEN
        assertEquals(store, result);
        verify(storeDao).findById(id);
    }

    @Test
    void testUpdateStore() {
        // GIVEN
        Store store = new Store();
        store.setStoreId(1L);
        store.setStoreName("Test Store");

        // WHEN
        storeService.updateStore(store);

        // THEN
        verify(storeDao).update(store);
    }

    @Test
    void testDeleteStore() {
        // GIVEN
        Long id = 1L;

        // WHEN
        storeService.deleteStore(id);

        // THEN
        verify(storeDao).delete(id);
    }

    @Test
    void testGetAllStores() {
        // GIVEN
        Store store1 = new Store();
        Store store2 = new Store();
        when(storeDao.findAll()).thenReturn(List.of(store1, store2));

        // WHEN
        List<Store> result = storeService.getAllStores();

        // THEN
        assertEquals(2, result.size());
        verify(storeDao).findAll();
    }

    @Test
    void testExportStoresToJson() throws IOException {
        // GIVEN
        Store store = new Store();
        when(storeDao.findAll()).thenReturn(List.of(store));

        // WHEN
        byte[] result = storeService.exportStoresToJson();

        // THEN
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testImportStoresFromJson() throws IOException {
        // GIVEN
        String jsonData = "[{\"storeId\":1,\"name\":\"Test Store\"}]";
        byte[] data = jsonData.getBytes();
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setStoreId(1L);
        storeDTO.setStoreName("Test Store");
        when(storeDao.create(any(Store.class))).thenReturn(new Store());

        // WHEN
        List<StoreDTO> result = storeService.importStoresFromJson(data);

        // THEN
        assertEquals(1, result.size());
        assertEquals(storeDTO.getStoreId(), result.get(0).getStoreId());
        verify(storeDao).create(any(Store.class));
    }
}
