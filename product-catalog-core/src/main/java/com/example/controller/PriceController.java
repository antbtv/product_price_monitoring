package com.example.controller;

import com.example.dto.HistoryRequestDTO;
import com.example.dto.PriceDTO;
import com.example.dto.PriceHistoryDTO;
import com.example.dto.PricePartialUpdateDTO;
import com.example.entity.Price;
import com.example.entity.PriceHistory;
import com.example.mapper.PriceHistoryMapper;
import com.example.mapper.PriceMapper;
import com.example.service.PriceService;
import com.example.service.ProductService;
import com.example.service.StoreService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/prices")
public class PriceController {

    private final PriceService priceService;
    private final ProductService productService;
    private final StoreService storeService;

    public PriceController(PriceService priceService,
                           ProductService productService, StoreService storeService) {
        this.priceService = priceService;
        this.productService = productService;
        this.storeService = storeService;
    }

    @PostMapping
    public ResponseEntity<PriceDTO> createPrice(@RequestBody Price price) {
        priceService.createPrice(price);

        PriceDTO priceDTO = PriceMapper.INSTANCE.toDto(price);
        return ResponseEntity.status(HttpStatus.CREATED).body(priceDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PriceDTO> getPriceById(@PathVariable Long id) {
        Price price = priceService.getPriceById(id);
        if (price == null) {
            return ResponseEntity.notFound().build();
        }

        PriceDTO priceDTO = PriceMapper.INSTANCE.toDto(price);
        return ResponseEntity.ok(priceDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PriceDTO> updatePrice(@PathVariable Long id, @RequestBody Price price) {
        price.setPriceId(id);
        priceService.updatePrice(price);

        PriceDTO priceDTO = PriceMapper.INSTANCE.toDto(price);
        return ResponseEntity.ok(priceDTO);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<PriceDTO> partialUpdateCategory(@PathVariable Long id,
                                                             @RequestBody PricePartialUpdateDTO updateDTO) {
        Price price = priceService.getPriceById(id);
        if (price == null) {
            return ResponseEntity.notFound().build();
        }

        if (updateDTO.getProductId() != null) {
            price.setProduct(productService.getProductById(updateDTO.getProductId()));
        }
        if (updateDTO.getStoreId() != null) {
            price.setStore(storeService.getStoreById(updateDTO.getStoreId()));
        }
        if (updateDTO.getPrice() != null) {
            price.setPrice(price.getPrice() + updateDTO.getPrice());
        }

        priceService.updatePrice(price);
        PriceDTO priceDTO = PriceMapper.INSTANCE.toDto(price);
        return ResponseEntity.ok(priceDTO);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrice(@PathVariable Long id) {
        priceService.deletePrice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PriceDTO>> getAllPrices() {
        List<Price> prices = priceService.getAllPrices();

        List<PriceDTO> priceDTOS = PriceMapper.INSTANCE.toDtoList(prices);
        return ResponseEntity.ok(priceDTOS);
    }

    @GetMapping("/compare/{productId}")
    public ResponseEntity<List<PriceDTO>> comparePrices(@PathVariable Long productId) {
        List<PriceDTO> prices = priceService.getPricesByProductId(productId);
        if (prices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(prices);
    }

    @GetMapping("/history/{productId}")
    public ResponseEntity<List<PriceHistoryDTO>> getPriceHistory(@PathVariable Long productId,
                                                                 @RequestBody HistoryRequestDTO request) {
        List<PriceHistoryDTO> priceHistoryDTOS = priceService.getPriceHistoryByProductIdAndDataRange(
                        productId,
                        request.getStoreId(),
                        request.getStartDate(),
                        request.getEndDate()
                ).stream()
                .map(PriceHistoryMapper.INSTANCE::toDto)
                .collect(Collectors.toList());

        if (priceHistoryDTOS.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(priceHistoryDTOS);
        }
    }

    @GetMapping("/history/chart/{productId}")
    public ResponseEntity<byte[]> getPriceHistoryChart(@PathVariable Long productId,
                                                       @RequestBody HistoryRequestDTO request) {
        List<PriceHistory> priceHistory = priceService.getPriceHistoryByProductIdAndDataRange(
                productId,
                request.getStoreId(),
                request.getStartDate(),
                request.getEndDate()
        );

        if (priceHistory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        TimeSeries series = new TimeSeries("Price");
        for (PriceHistory price : priceHistory) {
            series.add(new Second(price.getRecordedAt().getSecond(),
                    price.getRecordedAt().getMinute(),
                    price.getRecordedAt().getHour(),
                    price.getRecordedAt().getDayOfMonth(),
                    price.getRecordedAt().getMonthValue(),
                    price.getRecordedAt().getYear()), price.getPrice());
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Price History",
                "Time",
                "Price",
                dataset,
                false,
                true,
                false
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ChartUtils.writeChartAsPNG(baos, chart, 800, 600);
            byte[] chartBytes = baos.toByteArray();

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=price_chart.png")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(chartBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
