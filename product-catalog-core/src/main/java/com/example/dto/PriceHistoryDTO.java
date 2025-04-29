package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistoryDTO {

    private Long priceHistoryId;
    private Long productId;
    private Long storeId;
    private Integer price;
    private LocalDateTime recordedAt;
}
