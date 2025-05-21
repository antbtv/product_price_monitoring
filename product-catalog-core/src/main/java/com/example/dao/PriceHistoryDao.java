package com.example.dao;

import com.example.entity.PriceHistory;

import java.time.LocalDate;
import java.util.List;

public interface PriceHistoryDao extends GenericDao<PriceHistory> {

    /**
     * Получение списка цен на продукт в определённом магазине в
     * промежутке времени
     * @param productId id продукта
     * @param storeId id магазина
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return список цен
     */
    List<PriceHistory> findPriceHistoryByProductAndDateRange(Long productId,
                                                               Long storeId,
                                                               LocalDate startDate,
                                                               LocalDate endDate);
}
