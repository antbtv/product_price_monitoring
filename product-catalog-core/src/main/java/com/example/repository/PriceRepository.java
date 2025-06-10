package com.example.repository;

import com.example.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

    /**
     * Получение всех цен с загрузкой связанных сущностей: Product и Store
     */
    @Query("SELECT p FROM Price p JOIN FETCH p.product JOIN FETCH p.store")
    List<Price> findAllWithProductAndStore();

    /**
     * Получение всех цен по продукту
     *
     * @param productId id продукта
     * @return список цен
     */
    List<Price> findByProduct_ProductId(Long productId);
}