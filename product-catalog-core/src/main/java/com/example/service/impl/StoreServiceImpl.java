package com.example.service.impl;

import com.example.dao.StoreDao;
import com.example.dto.StoreDTO;
import com.example.entity.Store;
import com.example.exceptions.DataExportException;
import com.example.exceptions.DataImportException;
import com.example.exceptions.StoreNotFoundException;
import com.example.mapper.StoreMapper;
import com.example.service.StoreService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreDao storeDao;
    private final StoreMapper storeMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public Store createStore(Store store) {
        Store createdStore = storeDao.create(store);
        log.info("Магазин создан: ID={}, название='{}', адрес='{}'",
                createdStore.getStoreId(),
                createdStore.getStoreName(),
                createdStore.getAddress());
        return createdStore;
    }

    @Transactional(readOnly = true)
    @Override
    public Store getStoreById(Long id) {
        Store store = storeDao.findById(id);
        if (store == null) {
            log.warn("Магазин с ID={} не найден", id);
            throw new StoreNotFoundException(id);
        }
        log.debug("Получен магазин ID={}", id);
        return store;
    }

    @Transactional
    @Override
    public void updateStore(Store store) {
        if (storeDao.findById(store.getStoreId()) == null) {
            throw new StoreNotFoundException(store.getStoreId());
        }
        storeDao.update(store);
        log.info("Магазин обновлен ID={}", store.getStoreId());
    }

    @Transactional
    @Override
    public void deleteStore(Long id) {
        if (storeDao.findById(id) == null) {
            throw new StoreNotFoundException(id);
        }
        storeDao.delete(id);
        log.info("Магазин удален ID={}", id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Store> getAllStores() {
        List<Store> stores = storeDao.findAll();
        log.debug("Получено {} магазинов", stores.size());
        return stores;
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] exportStoresToJson() {
        try {
            List<StoreDTO> storeDTOS = storeMapper.toDtoList(storeDao.findAll());
            log.info("Экспортировано {} магазинов в JSON", storeDTOS.size());
            return objectMapper.writeValueAsBytes(storeDTOS);
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации категорий", e);
            throw new DataExportException("Ошибка экспорта категорий");
        }
    }

    @Transactional
    @Override
    public List<StoreDTO> importStoresFromJson(byte[] data) {
        try {
            List<StoreDTO> storeDTOS = objectMapper.readValue(data, new TypeReference<>() {});

            List<Store> stores = storeMapper.toEntityList(storeDTOS);
            stores.forEach(storeDao::create);

            log.info("Импортировано {} магазинов из JSON", stores.size());
            return storeDTOS;
        } catch (IOException e) {
            log.error("Ошибка десериализации категорий", e);
            throw new DataImportException("Ошибка импорта категорий");
        }
    }
}