package com.example.portfoliobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoldingDTO {
    private Long holdingId;
    private Long portfolioId;
    private String assetName;
    private String assetType;
    private BigDecimal quantity;
    private BigDecimal purchasePrice;
    private BigDecimal currentPrice;
    private String currency;
    private LocalDate purchaseDate;
    private BigDecimal targetValue;
}
