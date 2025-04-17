package com.example.service;

import com.example.entity.Price;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PriceService {

    void createPrice(Price price);

    Price getPriceById(int id);

    void updatePrice(Price price);

    void deletePrice(int id);

    List<Price> getAllPrices();
}
