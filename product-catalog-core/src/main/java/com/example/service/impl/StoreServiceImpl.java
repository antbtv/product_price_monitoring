package com.example.service.impl;

import com.example.dao.StoreDao;
import com.example.dto.StoreDTO;
import com.example.entity.Store;
import com.example.mapper.StoreMapper;
import com.example.service.StoreService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@Slf4j
public class StoreServiceImpl implements StoreService {

    private final StoreDao storeDao;
    private final StoreMapper storeMapper;
    private final ObjectMapper objectMapper;

    public StoreServiceImpl(StoreDao storeDao, StoreMapper storeMapper,
                            ObjectMapper objectMapper) {
        this.storeDao = storeDao;
        this.storeMapper = storeMapper;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @Override
    public Store createStore(Store store) {
        try {
            Store createdStore = storeDao.create(store);
            log.info("Магазин создан: ID={}, название='{}', адрес='{}'",
                    createdStore.getStoreId(),
                    createdStore.getStoreName(),
                    createdStore.getAddress());
            return createdStore;
        } catch (Exception e) {
            log.error("Ошибка создания магазина. Название: '{}'. Ошибка: {}",
                    store.getStoreName(),
                    e.getMessage());
            throw new RuntimeException("Ошибка при создании магазина", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Store getStoreById(Long id) {
        try {
            Store store = storeDao.findById(id);
            if (store == null) {
                log.info("Магазин с ID={} не найден", id);
                throw new RuntimeException("Магазин не найден");
            }
            log.debug("Получен магазин: ID={}, название='{}'", id, store.getStoreName());
            return store;
        } catch (Exception e) {
            log.error("Ошибка получения магазина ID={}. Ошибка: {}", id, e.getMessage());
            throw new RuntimeException("Ошибка при получении магазина", e);
        }
    }

    @Transactional
    @Override
    public void updateStore(Store store) {
        try {
            storeDao.update(store);
            log.info("Магазин обновлен: ID={}, новое название='{}'",
                    store.getStoreId(),
                    store.getStoreName());
        } catch (Exception e) {
            log.error("Ошибка обновления магазина ID={}. Ошибка: {}",
                    store.getStoreId(),
                    e.getMessage());
            throw new RuntimeException("Ошибка при обновлении магазина", e);
        }
    }

    @Transactional
    @Override
    public void deleteStore(Long id) {
        try {
            storeDao.delete(id);
            log.info("Магазин удален: ID={}", id);
        } catch (Exception e) {
            log.error("Ошибка удаления магазина ID={}. Ошибка: {}", id, e.getMessage());
            throw new RuntimeException("Ошибка при удалении магазина", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Store> getAllStores() {
        try {
            List<Store> stores = storeDao.findAll();
            log.debug("Получен список магазинов. Найдено {} элементов", stores.size());
            return stores;
        } catch (Exception e) {
            log.error("Ошибка получения списка магазинов. Ошибка: {}", e.getMessage());
            throw new RuntimeException("Ошибка при получении списка магазинов", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] exportStoresToJson() {
        try {
            List<Store> stores = storeDao.findAll();
            List<StoreDTO> storeDTOS = storeMapper.toDtoList(stores);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            objectMapper.writeValue(outputStream, storeDTOS);

            log.info("Экспортировано {} магазинов в JSON", stores.size());
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Ошибка экспорта магазинов. Ошибка: {}", e.getMessage());
            throw new RuntimeException("Ошибка при экспорте магазинов", e);
        }
    }

    @Transactional
    @Override
    public List<StoreDTO> importStoresFromJson(byte[] data) {
        try {
            List<StoreDTO> storeDTOS = objectMapper.readValue(
                    data,
                    new TypeReference<>() {}
            );

            List<Store> stores = storeMapper.toEntityList(storeDTOS);
            stores.forEach(storeDao::create);

            log.info("Импортировано {} магазинов из JSON", stores.size());
            return storeDTOS;
        } catch (Exception e) {
            log.error("Ошибка импорта магазинов. Ошибка: {}", e.getMessage());
            throw new RuntimeException("Ошибка при импорте магазинов", e);
        }
    }
}