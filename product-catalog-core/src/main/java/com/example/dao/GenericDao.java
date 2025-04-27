    package com.example.dao;

import java.util.List;

/**
 * Интерфейс, предоставляющий методы для работы с базой данных
 *
 * @param <T> тип сущности
 */
public interface GenericDao<T> {

    /**
     * Метод создания сущности в базе данных
     *
     * @param entity сущность, которую надо создать
     */
    T create(T entity);

    /**
     * Поиск сущности по заданному id
     *
     * @param id id сущности
     * @return сущности с заданным id
     */
    T findById(Long id);

    /**
     * Обновление сущности в базе данных
     *
     * @param entity сущность с обновленными данными
     */
    void update(T entity);

    /**
     * Удаление сущности с заданным id
     *
     * @param id id сущности
     */
    void delete(Long id);

    /**
     * Поиск всех сущностей в базе данных
     *
     * @return список сущностей
     */
    List<T> findAll();
}
