package com.example.service.impl;

import com.example.MessageSources;
import com.example.dao.PriceDao;
import com.example.dao.PriceHistoryDao;
import com.example.dto.PriceDTO;
import com.example.dto.StoreDTO;
import com.example.entity.Price;
import com.example.entity.PriceHistory;
import com.example.mapper.PriceMapper;
import com.example.service.PriceService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PriceServiceImpl implements PriceService {

    private final PriceDao priceDao;
    private final PriceHistoryDao priceHistoryDao;
    private static final Logger logger = LogManager.getLogger(PriceServiceImpl.class);

    public PriceServiceImpl(PriceDao priceDao, PriceHistoryDao priceHistoryDao) {
        this.priceDao = priceDao;
        this.priceHistoryDao = priceHistoryDao;
    }

    @Transactional
    @Override
    public Price createPrice(Price price) {
        try {
            return priceDao.create(price);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_CREATE);
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Price getPriceById(Long id) {
        try {
            return priceDao.findById(id);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_ONE);
            return null;
        }
    }

    @Transactional
    @Override
    public void updatePrice(Price price) {
        try {
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
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_UPDATE);
        }
    }

    @Transactional
    @Override
    public void deletePrice(Long id) {
        try {
            priceDao.delete(id);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_DELETE);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Price> getAllPrices() {
        try {
            return priceDao.findAll();
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_MANY);
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<PriceDTO> getPricesByProductId(Long productId) {
        List<Price> prices = priceDao.findByProductId(productId);
        return PriceMapper.INSTANCE.toDtoList(prices);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PriceHistory> getPriceHistoryByProductIdAndDataRange(Long productId,
                                                                     Long storeId,
                                                                     LocalDate startDate,
                                                                     LocalDate endDate) {
        return priceHistoryDao.findPriceHistoryByProductAndDateRange(productId, storeId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] exportPricesToJson() throws IOException {
        List<Price> prices = priceDao.findAll();
        List<PriceDTO> priceDTOS = PriceMapper.INSTANCE.toDtoList(prices);

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        objectMapper.writeValue(outputStream, priceDTOS);
        return outputStream.toByteArray();
    }

    @Transactional
    @Override
    public List<PriceDTO> importPricesFromJson(byte[] data) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<PriceDTO> priceDTOS = objectMapper.readValue(
                data,
                new TypeReference<>() {
                }
        );

        List<Price> prices = PriceMapper.INSTANCE.toEntityList(priceDTOS);
        prices.forEach(priceDao::create);

        return priceDTOS;
    }
}
