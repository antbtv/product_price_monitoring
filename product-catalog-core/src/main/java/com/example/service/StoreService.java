package com.example.service;

import com.example.entity.Store;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StoreService {

    void createStore(Store store);

    Store getStoreById(int id);

    void updateStore(Store store);

    void deleteStore(int id);

    List<Store> getAllStores();
}
