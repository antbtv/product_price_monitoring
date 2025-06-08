package com.example.service.impl;

import com.example.utils.ChartGenerator;
import com.example.dto.PriceDTO;
import com.example.entity.Price;
import com.example.entity.PriceHistory;
import com.example.exceptions.DataExportException;
import com.example.exceptions.DataImportException;
import com.example.exceptions.PriceHistoryNotFoundException;
import com.example.exceptions.PriceNotFoundException;
import com.example.mapper.PriceMapper;
import com.example.repository.PriceHistoryRepository;
import com.example.repository.PriceRepository;
import com.example.service.PriceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final ChartGenerator chartGenerator;
    private final PriceMapper priceMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public Price createPrice(Price price) {
        Price createdPrice = priceRepository.save(price);
        log.info("Создана цена: ID={}, продукт ID={}, магазин ID={}, значение={}",
                createdPrice.getPriceId(),
                createdPrice.getProduct().getProductId(),
                createdPrice.getStore().getStoreId(),
                createdPrice.getPrice());

        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setProduct(createdPrice.getProduct());
        priceHistory.setStore(createdPrice.getStore());
        priceHistory.setPrice(createdPrice.getPrice());
        priceHistory.setRecordedAt(LocalDateTime.now());
        priceHistoryRepository.save(priceHistory);

        return createdPrice;
    }

    @Transactional(readOnly = true)
    @Override
    public Price getPriceById(Long id) {
        return priceRepository.findById(id)
                .orElseThrow(() -> new PriceNotFoundException(id));
    }

    @Transactional
    @Override
    public void updatePrice(Price price) {
        Price currentPrice = priceRepository.findById(price.getPriceId())
                .orElseThrow(() -> new PriceNotFoundException(price.getPriceId()));

        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setProduct(currentPrice.getProduct());
        priceHistory.setStore(currentPrice.getStore());
        priceHistory.setPrice(currentPrice.getPrice());
        priceHistory.setRecordedAt(LocalDateTime.now());
        priceHistoryRepository.save(priceHistory);

        priceRepository.save(price);
        log.info("Обновлена цена ID={}", price.getPriceId());
    }

    @Transactional
    @Override
    public void deletePrice(Long id) {
        if (!priceRepository.existsById(id)) {
            throw new PriceNotFoundException(id);
        }
        priceRepository.deleteById(id);
        log.info("Удалена цена ID={}", id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Price> getAllPrices() {
        List<Price> prices = priceRepository.findAllWithProductAndStore();
        log.debug("Получено {} цен", prices.size());
        return prices;
    }

    @Transactional(readOnly = true)
    @Override
    public List<PriceDTO> getPricesByProductId(Long productId) {
        List<Price> prices = priceRepository.findByProduct_ProductId(productId);
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
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<PriceHistory> history = priceHistoryRepository.findByProductIdAndStoreIdAndDateRange(
                productId, storeId, startDateTime, endDateTime);

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
        for (int i = 0; i < 4; i++) {
            System.out.println("Hello world;");
            System.out.println("Hello world;");
            System.out.println("Hello world;");
            System.out.println("Hello world;");
            System.out.println("Hello world;");
            System.out.println("Hello world;");


        }


        try {
            List<PriceDTO> priceDTOs = priceMapper.toDtoList(priceRepository.findAllWithProductAndStore());
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
            priceRepository.saveAll(prices);
            log.info("Импортировано {} цен", prices.size());
            return priceDTOs;
        } catch (IOException e) {
            log.error("Ошибка десериализации категорий", e);
            throw new DataImportException("Ошибка импорта категорий");
        }
    }
}