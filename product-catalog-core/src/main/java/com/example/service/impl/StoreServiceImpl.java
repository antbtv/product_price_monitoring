package com.example.service.impl;

import com.example.dao.StoreDao;
import com.example.entity.Store;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StoreServiceImpl {

    private final StoreDao storeDao;

    public StoreServiceImpl(StoreDao storeDao) {
        this.storeDao = storeDao;
    }

    @Transactional
    public void createStore(Store store) {
        storeDao.create(store);
    }

    @Transactional(readOnly = true)
    public Store getStoreById(int id) {
        return storeDao.findById(id);
    }

    @Transactional
    public void updateStore(Store store) {
        storeDao.update(store);
    }

    @Transactional
    public void deleteStore(int id) {
        storeDao.delete(id);
    }

    @Transactional(readOnly = true)
    public List<Store> getAllStores() {
        return storeDao.findAll();
    }
}
