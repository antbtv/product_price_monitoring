package com.example.repository;

import com.example.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    /**
     * Получить историю цен по продукту и магазину в заданном диапазоне дат
     *
     * @param productId ID продукта
     * @param storeId ID магазина
     * @param startDate начало диапазона
     * @param endDate конец диапазона
     * @return список записей истории цен
     */
    @Query("SELECT ph FROM PriceHistory ph " +
            "WHERE ph.product.productId = :productId " +
            "AND ph.store.storeId = :storeId " +
            "AND ph.recordedAt BETWEEN :startDate AND :endDate")
    List<PriceHistory> findByProductIdAndStoreIdAndDateRange(
            @Param("productId") Long productId,
            @Param("storeId") Long storeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
