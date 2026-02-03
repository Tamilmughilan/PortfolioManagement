package com.example.portfoliobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSnapshotDTO {
    private Long snapshotId;
    private Long portfolioId;
    private BigDecimal totalValue;
    private String currency;
    private LocalDate snapshotDate;
}

