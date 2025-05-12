package com.example.service.impl;

import com.example.MessageSources;
import com.example.dao.StoreDao;
import com.example.dto.StoreDTO;
import com.example.entity.Store;
import com.example.mapper.StoreMapper;
import com.example.service.StoreService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreDao storeDao;
    private static final Logger logger = LogManager.getLogger(StoreServiceImpl.class);

    public StoreServiceImpl(StoreDao storeDao) {
        this.storeDao = storeDao;
    }

    @Transactional
    @Override
    public Store createStore(Store store) {
        try {
            return storeDao.create(store);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_CREATE);
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Store getStoreById(Long id) {
        try {
            return storeDao.findById(id);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_ONE);
            return null;
        }
    }

    @Transactional
    @Override
    public void updateStore(Store store) {
        try {
            storeDao.update(store);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_UPDATE);
        }
    }

    @Transactional
    @Override
    public void deleteStore(Long id) {
        try {
            storeDao.delete(id);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_DELETE);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Store> getAllStores() {
        try {
            return storeDao.findAll();
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_MANY);
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] exportStoresToJson() throws IOException {
        List<Store> stores = storeDao.findAll();
        List<StoreDTO> storeDTOS = StoreMapper.INSTANCE.toDtoList(stores);

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        objectMapper.writeValue(outputStream, storeDTOS);
        return outputStream.toByteArray();
    }

    @Transactional
    @Override
    public List<StoreDTO> importStoresFromJson(byte[] data) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<StoreDTO> storeDTOS = objectMapper.readValue(
                data,
                new TypeReference<>() {
                }
        );

        List<Store> stores = StoreMapper.INSTANCE.toEntityList(storeDTOS);
        stores.forEach(storeDao::create);
        return storeDTOS;
    }
}
