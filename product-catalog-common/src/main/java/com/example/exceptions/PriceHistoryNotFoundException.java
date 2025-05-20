package com.example.exceptions;

public class PriceHistoryNotFoundException extends RuntimeException {
    public PriceHistoryNotFoundException(Long productId, Long storeId) {
        super(String.format("История цен не найдена для продукта ID=%d и магазина ID=%d",
                productId, storeId));
    }
}