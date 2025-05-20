package com.example.service;

import com.example.dto.PriceDTO;
import com.example.entity.Price;
import com.example.entity.PriceHistory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Интерфейс для управления ценами на товары
 */
public interface PriceService {

    /**
     * Создает новую запись цены
     *
     * @param price сущность цены для создания
     * @return созданная сущность цены
     */
    Price createPrice(Price price);

    /**
     * Получает цену по id
     *
     * @param id id цены
     * @return найденная сущность цены
     */
    Price getPriceById(Long id);

    /**
     * Обновляет существующую цену
     *
     * @param price сущность цены с обновленными данными
     */
    void updatePrice(Price price);

    /**
     * Удаляет цену по id
     *
     * @param id id цены для удаления
     */
    void deletePrice(Long id);

    /**
     * Получает список всех цен
     *
     * @return список сущностей цен
     */
    List<Price> getAllPrices();

    /**
     * Получает цены по id товара
     * 
     * @param productId id товара
     * @return список DTO цен
     */
    List<PriceDTO> getPricesByProductId(Long productId);

    /**
     * Получает историю цен по id товара и магазина за указанный период
     * 
     * @param productId id товара
     * @param storeId id магазина
     * @param startDate начальная дата периода
     * @param endDate конечная дата периода
     * @return список сущностей истории цен
     */
    List<PriceHistory> getPriceHistoryByProductIdAndDataRange(Long productId,
                                                              Long storeId,
                                                              LocalDate startDate,
                                                              LocalDate endDate);

    /**
     * Генерирует график истории цен в виде изображения
     * 
     * @param productId id товара
     * @param storeId id магазина
     * @param startDate начальная дата периода
     * @param endDate конечная дата периода
     * @return массив байтов с изображением графика
     * @throws IOException при ошибках генерации графика
     */
    byte[] generatePriceHistoryChart(Long productId, Long storeId,
                                     LocalDate startDate, LocalDate endDate) throws IOException;

    /**
     * Экспортирует цены в JSON-формате
     * 
     * @return массив байтов с данными в JSON-формате
     * @throws IOException при ошибках ввода-вывода
     */
    byte[] exportPricesToJson() throws IOException;

    /**
     * Импортирует цены из JSON-данных
     * 
     * @param data массив байтов с JSON-данными
     * @return список импортированных DTO цен
     * @throws IOException при ошибках парсинга или ввода-вывода
     */
    List<PriceDTO> importPricesFromJson(byte[] data) throws IOException;
}