package com.example.service;

import com.example.entity.Store;

import java.util.List;

public interface StoreService {

    Store createStore(Store store);

    Store getStoreById(Long id);

    void updateStore(Store store);

    void deleteStore(Long id);

    List<Store> getAllStores();
}
