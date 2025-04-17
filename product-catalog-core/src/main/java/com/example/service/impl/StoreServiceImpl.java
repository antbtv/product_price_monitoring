package com.example.service.impl;

import com.example.dao.StoreDao;
import com.example.entity.Store;
import com.example.service.StoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreDao storeDao;

    public StoreServiceImpl(StoreDao storeDao) {
        this.storeDao = storeDao;
    }

    @Transactional
    @Override
    public void createStore(Store store) {
        storeDao.create(store);
    }

    @Transactional(readOnly = true)
    @Override
    public Store getStoreById(int id) {
        return storeDao.findById(id);
    }

    @Transactional
    @Override
    public void updateStore(Store store) {
        storeDao.update(store);
    }

    @Transactional
    @Override
    public void deleteStore(int id) {
        storeDao.delete(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Store> getAllStores() {
        return storeDao.findAll();
    }
}
