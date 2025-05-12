package com.example.service;

import com.example.dto.StoreDTO;
import com.example.entity.Store;

import java.io.IOException;
import java.util.List;

public interface StoreService {

    Store createStore(Store store);

    Store getStoreById(Long id);

    void updateStore(Store store);

    void deleteStore(Long id);

    List<Store> getAllStores();

    byte[] exportStoresToJson() throws IOException;

    List<StoreDTO> importStoresFromJson(byte[] data) throws IOException;
}
