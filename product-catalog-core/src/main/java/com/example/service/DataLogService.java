package com.example.service;

import com.example.entity.security.User;

/**
 * Сервис для логирования импорта/экспорта данных
 */
public interface DataLogService {
    /**
     * Логирование операции
     *
     * @param operationType тип операции
     * @param tableName имя таблицы
     * @param recordCount количество переданных записей
     * @param user id пользователя
     */
    void logOperation(String operationType, String tableName, Long recordCount, User user);
}
