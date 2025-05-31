package com.example.service;

import com.example.dto.StoreDTO;
import com.example.entity.Store;

import java.io.IOException;
import java.util.List;

/**
 * Интерфейс для управления магазинами в системе
 */
public interface StoreService {

    /**
     * Создает новый магазин
     *
     * @param store сущность магазина для создания
     * @return созданная сущность магазина
     */
    Store createStore(Store store);

    /**
     * Получает магазин по id
     *
     * @param id id магазина
     * @return найденная сущность магазина
     */
    Store getStoreById(Long id);

    /**
     * Обновляет существующий магазин
     *
     * @param store сущность магазина с обновленными данными
     */
    void updateStore(Store store);

    /**
     * Удаляет магазин по id
     *
     * @param id id магазина для удаления
     */
    void deleteStore(Long id);

    /**
     * Получает список всех магазинов
     * 
     * @return список сущностей магазинов
     */
    List<Store> getAllStores();

    /**
     * Экспортирует магазины в JSON-формате
     * 
     * @return массив байтов с данными в JSON-формате
     * @throws IOException при ошибках ввода-вывода
     */
    byte[] exportStoresToJson() throws IOException;

    /**
     * Импортирует магазины из JSON-данных
     * 
     * @param data массив байтов с JSON-данными
     * @return список импортированных DTO магазинов
     * @throws IOException при ошибках парсинга или ввода-вывода
     */
    List<StoreDTO> importStoresFromJson(byte[] data) throws IOException;
}