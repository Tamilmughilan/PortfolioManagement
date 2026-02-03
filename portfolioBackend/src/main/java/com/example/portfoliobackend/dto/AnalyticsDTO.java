package com.example.portfoliobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDTO {
    private BigDecimal totalMarketValue;
    private BigDecimal totalCost;
    private BigDecimal totalGainLoss;
    private Map<String, BigDecimal> allocationValues;
    private Map<String, BigDecimal> allocationPercentages;
    private Map<String, BigDecimal> targetDrift;
}
