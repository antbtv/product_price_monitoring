package com.example.service.impl;

import com.example.chart.ChartGenerator;
import com.example.dao.PriceDao;
import com.example.dao.PriceHistoryDao;
import com.example.dto.PriceDTO;
import com.example.entity.Price;
import com.example.entity.PriceHistory;
import com.example.exceptions.DataExportException;
import com.example.exceptions.DataImportException;
import com.example.exceptions.PriceHistoryNotFoundException;
import com.example.exceptions.PriceNotFoundException;
import com.example.mapper.PriceMapper;
import com.example.service.PriceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class PriceServiceImpl implements PriceService {

    private final PriceDao priceDao;
    private final PriceHistoryDao priceHistoryDao;
    private final ChartGenerator chartGenerator;
    private final PriceMapper priceMapper;
    private final ObjectMapper objectMapper;

    public PriceServiceImpl(PriceDao priceDao, PriceHistoryDao priceHistoryDao,
                            PriceMapper priceMapper, ObjectMapper objectMapper,
                            ChartGenerator chartGenerator) {
        this.priceDao = priceDao;
        this.priceHistoryDao = priceHistoryDao;
        this.priceMapper = priceMapper;
        this.objectMapper = objectMapper;
        this.chartGenerator = chartGenerator;
    }

    @Transactional
    @Override
    public Price createPrice(Price price) {
        Price createdPrice = priceDao.create(price);
        log.info("Создана цена: ID={}, продукт ID={}, магазин ID={}, значение={}",
                createdPrice.getPriceId(),
                createdPrice.getProduct().getProductId(),
                createdPrice.getStore().getStoreId(),
                createdPrice.getPrice());
        return createdPrice;
    }

    @Transactional(readOnly = true)
    @Override
    public Price getPriceById(Long id) {
        Price price = priceDao.findById(id);
        if (price == null) {
            throw new PriceNotFoundException(id);
        }
        log.debug("Получена цена ID={}", id);
        return price;
    }

    @Transactional
    @Override
    public void updatePrice(Price price) {
        Price currentPrice = priceDao.findById(price.getPriceId());
        if (currentPrice == null) {
            throw new PriceNotFoundException(price.getPriceId());
        }

        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setProduct(currentPrice.getProduct());
        priceHistory.setStore(currentPrice.getStore());
        priceHistory.setPrice(currentPrice.getPrice());
        priceHistory.setRecordedAt(LocalDateTime.now());
        priceHistoryDao.create(priceHistory);

        priceDao.update(price);
        log.info("Обновлена цена ID={}", price.getPriceId());
    }

    @Transactional
    @Override
    public void deletePrice(Long id) {
        if (priceDao.findById(id) == null) {
            throw new PriceNotFoundException(id);
        }
        priceDao.delete(id);
        log.info("Удалена цена ID={}", id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Price> getAllPrices() {
        List<Price> prices = priceDao.findAll();
        log.debug("Получено {} цен", prices.size());
        return prices;
    }

    @Transactional(readOnly = true)
    @Override
    public List<PriceDTO> getPricesByProductId(Long productId) {
        List<Price> prices = priceDao.findByProductId(productId);
        if (prices.isEmpty()) {
            log.debug("Цены для продукта ID={} не найдены", productId);
        }
        return priceMapper.toDtoList(prices);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PriceHistory> getPriceHistoryByProductIdAndDataRange(Long productId,
                                                                     Long storeId,
                                                                     LocalDate startDate,
                                                                     LocalDate endDate) {
        List<PriceHistory> history = priceHistoryDao.findPriceHistoryByProductAndDateRange(
                productId, storeId, startDate, endDate);
        log.info("История цен: продукт ID={}, магазин ID={}, найдено {} записей",
                productId, storeId, history.size());
        return history;
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] generatePriceHistoryChart(Long productId, Long storeId,
                                            LocalDate startDate, LocalDate endDate) throws IOException {
        List<PriceHistory> priceHistory = getPriceHistoryByProductIdAndDataRange(
                productId, storeId, startDate, endDate);

        if (priceHistory.isEmpty()) {
            throw new PriceHistoryNotFoundException(productId, storeId);
        }

        return chartGenerator.generatePriceHistoryChart(priceHistory);
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] exportPricesToJson() {
        try {
            List<PriceDTO> priceDTOs = priceMapper.toDtoList(priceDao.findAll());
            log.info("Экспортировано {} цен", priceDTOs.size());
            return objectMapper.writeValueAsBytes(priceDTOs);
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации категорий", e);
            throw new DataExportException("Ошибка экспорта категорий");
        }
    }

    @Transactional
    @Override
    public List<PriceDTO> importPricesFromJson(byte[] data) {
        try {
            List<PriceDTO> priceDTOs = objectMapper.readValue(data, new TypeReference<>() {});
            List<Price> prices = priceMapper.toEntityList(priceDTOs);
            prices.forEach(priceDao::create);
            log.info("Импортировано {} цен", prices.size());
            return priceDTOs;
        } catch (IOException e) {
            log.error("Ошибка десериализации категорий", e);
            throw new DataImportException("Ошибка импорта категорий");
        }
    }
}