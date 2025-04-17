package com.example.service.impl;

import com.example.dao.PriceDao;
import com.example.entity.Price;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PriceServiceImpl {

    private final PriceDao priceDao;

    public PriceServiceImpl(PriceDao priceDao) {
        this.priceDao = priceDao;
    }

    @Transactional
    public void createPrice(Price price) {
        priceDao.create(price);
    }

    @Transactional(readOnly = true)
    public Price getPriceById(int id) {
        return priceDao.findById(id);
    }

    @Transactional
    public void updatePrice(Price price) {
        priceDao.update(price);
    }

    @Transactional
    public void deletePrice(int id) {
        priceDao.delete(id);
    }

    @Transactional(readOnly = true)
    public List<Price> getAllPrices() {
        return priceDao.findAll();
    }
}
