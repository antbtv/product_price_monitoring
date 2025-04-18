package com.example.service;

import com.example.entity.Price;

import java.util.List;

public interface PriceService {

    void createPrice(Price price);

    Price getPriceById(Long id);

    void updatePrice(Price price);

    void deletePrice(Long id);

    List<Price> getAllPrices();
}
