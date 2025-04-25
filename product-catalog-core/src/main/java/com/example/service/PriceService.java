package com.example.service;

import com.example.dto.PriceDTO;
import com.example.entity.Price;
import com.example.entity.PriceHistory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface PriceService {

    void createPrice(Price price);

    Price getPriceById(Long id);

    void updatePrice(Price price);

    void deletePrice(Long id);

    List<Price> getAllPrices();

    List<PriceDTO> getPricesByProductId(Long productId);

    void exportPricesToJson(String filePath) throws IOException;

    List<PriceHistory> getPriceHistoryByProductIdAndDataRange(Long productId,
                                                              Long storeId,
                                                              LocalDate startDate,
                                                              LocalDate endDate);
}
