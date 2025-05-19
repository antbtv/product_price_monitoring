package com.example.service.impl;

import com.example.chart.ChartGenerator;
import com.example.dao.PriceDao;
import com.example.dao.PriceHistoryDao;
import com.example.dto.PriceDTO;
import com.example.entity.Price;
import com.example.entity.PriceHistory;
import com.example.mapper.PriceMapper;
import com.example.service.PriceService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
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
        try {
            Price createdPrice = priceDao.create(price);
            log.info("Создана новая цена: ID={}, продукт ID={}, магазин ID={}, значение={}",
                    createdPrice.getPriceId(),
                    createdPrice.getProduct().getProductId(),
                    createdPrice.getStore().getStoreId(),
                    createdPrice.getPrice());
            return createdPrice;
        } catch (Exception e) {
            log.error("Ошибка создания цены для продукта ID={}. Ошибка: {}",
                    price.getProduct().getProductId(),
                    e.getMessage());
            throw new RuntimeException("Ошибка при создании цены", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Price getPriceById(Long id) {
        try {
            Price price = priceDao.findById(id);
            if (price == null) {
                log.info("Цена с ID={} не найдена", id);
                throw new RuntimeException("Цена не найдена");
            }
            log.info("Получена цена: ID={}, продукт={}, магазин={}, значение={}",
                    price.getPriceId(),
                    price.getProduct().getProductName(),
                    price.getStore().getStoreName(),
                    price.getPrice());
            return price;
        } catch (Exception e) {
            log.error("Ошибка получения цены ID={}. Ошибка: {}", id, e.getMessage());
            throw new RuntimeException("Ошибка при получении цены", e);
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
                priceHistory.setPrice(currentPrice.getPrice());
                priceHistory.setRecordedAt(LocalDateTime.now());
                priceHistoryDao.create(priceHistory);

                priceDao.update(price);

                log.info("Обновлена цена: ID={}, старое значение={}, новое значение={}",
                        price.getPriceId(),
                        currentPrice.getPrice(),
                        price.getPrice());
            }
        } catch (Exception e) {
            log.error("Ошибка обновления цены ID={}. Ошибка: {}",
                    price.getPriceId(),
                    e.getMessage());
            throw new RuntimeException("Ошибка при обновлении цены", e);
        }
    }

    @Transactional
    @Override
    public void deletePrice(Long id) {
        try {
            priceDao.delete(id);
            log.info("Удалена цена ID={}", id);
        } catch (Exception e) {
            log.error("Ошибка удаления цены ID={}. Ошибка: {}", id, e.getMessage());
            throw new RuntimeException("Ошибка при удалении цены", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Price> getAllPrices() {
        try {
            List<Price> prices = priceDao.findAll();
            log.info("Получен список цен. Найдено {} записей", prices.size());
            return prices;
        } catch (Exception e) {
            log.error("Ошибка получения списка цен. Ошибка: {}", e.getMessage());
            throw new RuntimeException("Ошибка при получении списка цен", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<PriceDTO> getPricesByProductId(Long productId) {
        try {
            List<Price> prices = priceDao.findByProductId(productId);
            log.info("Получены цены для продукта ID={}. Найдено {} записей",
                    productId, prices.size());
            return priceMapper.toDtoList(prices);
        } catch (Exception e) {
            log.error("Ошибка получения цен для продукта ID={}. Ошибка: {}",
                    productId, e.getMessage());
            throw new RuntimeException("Ошибка при получении цен по продукту", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<PriceHistory> getPriceHistoryByProductIdAndDataRange(Long productId,
                                                                     Long storeId,
                                                                     LocalDate startDate,
                                                                     LocalDate endDate) {
        try {
            List<PriceHistory> history = priceHistoryDao.findPriceHistoryByProductAndDateRange(
                    productId, storeId, startDate, endDate);
            log.info("Получена история цен. Продукт ID={}, магазин ID={}, период {}-{}. Найдено {} записей",
                    productId, storeId, startDate, endDate, history.size());
            return history;
        } catch (Exception e) {
            log.error("Ошибка получения истории цен. Продукт ID={}, магазин ID={}. Ошибка: {}",
                    productId, storeId, e.getMessage());
            throw new RuntimeException("Ошибка при получении истории цен", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] generatePriceHistoryChart(Long productId, Long storeId,
                                            LocalDate startDate, LocalDate endDate) {
        try {
            List<PriceHistory> priceHistory = getPriceHistoryByProductIdAndDataRange(
                    productId, storeId, startDate, endDate);

            byte[] chart = chartGenerator.generatePriceHistoryChart(priceHistory);
            log.info("Сгенерирован график цен. Продукт ID={}, магазин ID={}, период {}-{}",
                    productId, storeId, startDate, endDate);
            return chart;
        } catch (Exception e) {
            log.error("Ошибка генерации графика цен. Продукт ID={}, магазин ID={}. Ошибка: {}",
                    productId, storeId, e.getMessage());
            throw new RuntimeException("Ошибка при генерации графика цен", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] exportPricesToJson() {
        try {
            List<Price> prices = priceDao.findAll();
            List<PriceDTO> priceDTOS = priceMapper.toDtoList(prices);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            objectMapper.writeValue(outputStream, priceDTOS);

            log.info("Экспортированы цены. Всего {} записей", prices.size());
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Ошибка экспорта цен. Ошибка: {}", e.getMessage());
            throw new RuntimeException("Ошибка при экспорте цен", e);
        }
    }

    @Transactional
    @Override
    public List<PriceDTO> importPricesFromJson(byte[] data) {
        try {
            List<PriceDTO> priceDTOS = objectMapper.readValue(data, new TypeReference<>() {});
            List<Price> prices = priceMapper.toEntityList(priceDTOS);
            prices.forEach(priceDao::create);

            log.info("Импортированы цены. Всего {} записей", prices.size());
            return priceDTOS;
        } catch (Exception e) {
            log.error("Ошибка импорта цен. Ошибка: {}", e.getMessage());
            throw new RuntimeException("Ошибка при импорте цен", e);
        }
    }
}