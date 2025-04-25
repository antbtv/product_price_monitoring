package com.example.dao;

import com.example.entity.PriceHistory;

import java.time.LocalDate;
import java.util.List;

public interface PriceHistoryDao extends GenericDao<PriceHistory> {
    List<PriceHistory> findPriceHistoryByProductAndDateRange(Long productId,
                                                               Long storeId,
                                                               LocalDate startDate,
                                                               LocalDate endDate);
}
