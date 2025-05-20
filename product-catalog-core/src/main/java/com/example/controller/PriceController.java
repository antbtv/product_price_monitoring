package com.example.controller;

import com.example.dto.HistoryRequestDTO;
import com.example.dto.PriceDTO;
import com.example.dto.PriceHistoryDTO;
import com.example.dto.PriceCreateDTO;
import com.example.entity.Price;
import com.example.mapper.PriceHistoryMapper;
import com.example.mapper.PriceMapper;
import com.example.service.DataLogService;
import com.example.service.PriceService;
import com.example.service.ProductService;
import com.example.service.StoreService;
import com.example.service.security.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/prices")
public class PriceController {

    private final PriceService priceService;
    private final ProductService productService;
    private final StoreService storeService;
    private final PriceMapper priceMapper;
    private final PriceHistoryMapper priceHistoryMapper;
    private final UserService userService;
    private final DataLogService dataLogService;

    private static final Logger logger = LogManager.getLogger(PriceController.class);

    public PriceController(PriceService priceService, ProductService productService, 
                           StoreService storeService, PriceMapper priceMapper,
                           PriceHistoryMapper priceHistoryMapper, UserService userService,
                           DataLogService dataLogService) {
        this.priceService = priceService;
        this.productService = productService;
        this.storeService = storeService;
        this.priceMapper = priceMapper;
        this.priceHistoryMapper = priceHistoryMapper;
        this.userService = userService;
        this.dataLogService = dataLogService;
    }

    @PostMapping
    public ResponseEntity<PriceDTO> createPrice(@RequestBody PriceCreateDTO createDTO) {
        Price price = new Price(productService.getProductById(createDTO.getProductId()),
                storeService.getStoreById(createDTO.getStoreId()),
                createDTO.getPrice());
        Price createdPrice = priceService.createPrice(price);

        PriceDTO priceDTO = priceMapper.toDto(createdPrice);
        return ResponseEntity.status(HttpStatus.CREATED).body(priceDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PriceDTO> getPriceById(@PathVariable Long id) {
        Price price = priceService.getPriceById(id);
        if (price == null) {
            return ResponseEntity.notFound().build();
        }

        PriceDTO priceDTO = priceMapper.toDto(price);
        return ResponseEntity.ok(priceDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PriceDTO> updatePrice(@PathVariable Long id, @RequestBody Price price) {
        price.setPriceId(id);
        priceService.updatePrice(price);

        PriceDTO priceDTO = priceMapper.toDto(price);
        return ResponseEntity.ok(priceDTO);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<PriceDTO> partialUpdateCategory(@PathVariable Long id,
                                                             @RequestBody PriceCreateDTO updateDTO) {
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
            price.setPrice(updateDTO.getPrice());
        }

        priceService.updatePrice(price);
        PriceDTO priceDTO = priceMapper.toDto(price);
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

        List<PriceDTO> priceDTOS = priceMapper.toDtoList(prices);
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

    @PostMapping("/history/{productId}")
    public ResponseEntity<List<PriceHistoryDTO>> getPriceHistory(@PathVariable Long productId,
                                                                 @RequestBody HistoryRequestDTO request) {
        List<PriceHistoryDTO> priceHistoryDTOS = priceService.getPriceHistoryByProductIdAndDataRange(
                        productId,
                        request.getStoreId(),
                        request.getStartDate(),
                        request.getEndDate()
                ).stream()
                .map(priceHistoryMapper::toDto)
                .collect(Collectors.toList());

        if (priceHistoryDTOS.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(priceHistoryDTOS);
        }
    }

    @PostMapping("/history/chart/{productId}")
    public ResponseEntity<byte[]> getPriceHistoryChart(@PathVariable Long productId,
                                                       @RequestBody HistoryRequestDTO request) throws IOException{
            byte[] chartBytes = priceService.generatePriceHistoryChart(
                    productId,
                    request.getStoreId(),
                    request.getStartDate(),
                    request.getEndDate()
            );

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=price_chart.png")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(chartBytes);
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportPrices() throws IOException {
        byte[] data = priceService.exportPricesToJson();
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(data));

        dataLogService.logOperation("EXPORT", "prices",
                (long) priceService.getAllPrices().size(), userService.getCurrentUser());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"prices.json\"")
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(data.length)
                .body(resource);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PriceDTO>> importPrices(
            @RequestPart("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

            List<PriceDTO> priceDTOS = priceService.importPricesFromJson(file.getBytes());

            dataLogService.logOperation("IMPORT", "prices",
                    (long) priceDTOS.size(), userService.getCurrentUser());

            return ResponseEntity.ok(priceDTOS);
    }
}