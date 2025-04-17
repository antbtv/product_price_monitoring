package com.example.service.impl;

import com.example.dao.PriceDao;
import com.example.entity.Price;
import com.example.service.PriceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PriceServiceImpl implements PriceService {

    private final PriceDao priceDao;

    public PriceServiceImpl(PriceDao priceDao) {
        this.priceDao = priceDao;
    }

    @Transactional
    @Override
    public void createPrice(Price price) {
        priceDao.create(price);
    }

    @Transactional(readOnly = true)
    @Override
    public Price getPriceById(int id) {
        return priceDao.findById(id);
    }

    @Transactional
    @Override
    public void updatePrice(Price price) {
        priceDao.update(price);
    }

    @Transactional
    @Override
    public void deletePrice(int id) {
        priceDao.delete(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Price> getAllPrices() {
        return priceDao.findAll();
    }
}
