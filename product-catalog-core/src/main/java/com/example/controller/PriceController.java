package com.example.controller;

import com.example.dto.PriceDTO;
import com.example.entity.Price;
import com.example.mapper.PriceMapper;
import com.example.service.PriceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/prices")
public class PriceController {

    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
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
}
