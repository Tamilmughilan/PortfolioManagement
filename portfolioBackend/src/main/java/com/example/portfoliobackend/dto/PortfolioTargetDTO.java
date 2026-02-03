package com.example.portfoliobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioTargetDTO {
    private Long targetId;
    private Long portfolioId;
    private String assetType;
    private BigDecimal targetPercentage;
}

