package com.example.service.impl;

import com.example.dao.PriceDao;
import com.example.dao.PriceHistoryDao;
import com.example.dto.PriceDTO;
import com.example.dto.StoreDTO;
import com.example.entity.Price;
import com.example.entity.PriceHistory;
import com.example.entity.Store;
import com.example.mapper.PriceMapper;
import com.example.mapper.StoreMapper;
import com.example.service.PriceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PriceServiceImpl implements PriceService {

    private final PriceDao priceDao;
    private final PriceHistoryDao priceHistoryDao;

    public PriceServiceImpl(PriceDao priceDao, PriceHistoryDao priceHistoryDao) {
        this.priceDao = priceDao;
        this.priceHistoryDao = priceHistoryDao;
    }

    @Transactional
    @Override
    public Price createPrice(Price price) {
        return priceDao.create(price);
    }

    @Transactional(readOnly = true)
    @Override
    public Price getPriceById(Long id) {
        return priceDao.findById(id);
    }

    @Transactional
    @Override
    public void updatePrice(Price price) {
        Price currentPrice = priceDao.findById(price.getPriceId());
        if (currentPrice != null) {
            PriceHistory priceHistory = new PriceHistory();
            priceHistory.setProduct(currentPrice.getProduct());
            priceHistory.setStore(currentPrice.getStore());
            priceHistory.setPrice(price.getPrice());
            priceHistory.setRecordedAt(LocalDateTime.now());

            priceHistoryDao.create(priceHistory);

            priceDao.update(price);
        }
    }

    @Transactional
    @Override
    public void deletePrice(Long id) {
        priceDao.delete(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Price> getAllPrices() {
        return priceDao.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<PriceDTO> getPricesByProductId(Long productId) {
        List<Price> prices = priceDao.findByProductId(productId);
        return PriceMapper.INSTANCE.toDtoList(prices);
    }

    @Transactional(readOnly = true)
    @Override
    public void exportPricesToJson(String filePath) throws IOException {
        List<Price> prices = priceDao.findAll();
        List<PriceDTO> priceDTOS = PriceMapper.INSTANCE.toDtoList(prices);

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        objectMapper.writeValue(new File(filePath), priceDTOS);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PriceHistory> getPriceHistoryByProductIdAndDataRange(Long productId,
                                                                     Long storeId,
                                                                     LocalDate startDate,
                                                                     LocalDate endDate) {
        return priceHistoryDao.findPriceHistoryByProductAndDateRange(productId, storeId, startDate, endDate);
    }
}
