package com.example.service.impl;

import com.example.dao.StoreDao;
import com.example.dto.ProductDTO;
import com.example.dto.StoreDTO;
import com.example.entity.Product;
import com.example.entity.Store;
import com.example.mapper.ProductMapper;
import com.example.mapper.StoreMapper;
import com.example.service.StoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
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
    public Store getStoreById(Long id) {
        return storeDao.findById(id);
    }

    @Transactional
    @Override
    public void updateStore(Store store) {
        storeDao.update(store);
    }

    @Transactional
    @Override
    public void deleteStore(Long id) {
        storeDao.delete(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Store> getAllStores() {
        return storeDao.findAll();
    }

    @Transactional(readOnly = true)
    public void exportStoresToJson(String filePath) throws IOException {
        List<Store> stores = storeDao.findAll();
        List<StoreDTO> storeDTOS = StoreMapper.INSTANCE.toDtoList(stores);

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        objectMapper.writeValue(new File(filePath), storeDTOS);
    }
}
