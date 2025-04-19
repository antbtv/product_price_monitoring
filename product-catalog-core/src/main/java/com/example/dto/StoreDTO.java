package com.example.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreDTO {

    private Long storeId;
    private String storeName;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
