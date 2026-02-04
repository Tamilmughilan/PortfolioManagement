package com.example.portfoliobackend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PortfolioDashboardDTO {
    private Long portfolioId;
    private String portfolioName;
    private String baseCurrency;
    private BigDecimal totalValue;
    private List<HoldingDetailDTO> holdings;
    private LocalDateTime createdAt;

    public PortfolioDashboardDTO() {}

    public PortfolioDashboardDTO(Long portfolioId, String portfolioName, String baseCurrency,
                                BigDecimal totalValue, List<HoldingDetailDTO> holdings, LocalDateTime createdAt) {
        this.portfolioId = portfolioId;
        this.portfolioName = portfolioName;
        this.baseCurrency = baseCurrency;
        this.totalValue = totalValue;
        this.holdings = holdings;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getPortfolioId() { return portfolioId; }
    public void setPortfolioId(Long portfolioId) { this.portfolioId = portfolioId; }

    public String getPortfolioName() { return portfolioName; }
    public void setPortfolioName(String portfolioName) { this.portfolioName = portfolioName; }

    public String getBaseCurrency() { return baseCurrency; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public List<HoldingDetailDTO> getHoldings() { return holdings; }
    public void setHoldings(List<HoldingDetailDTO> holdings) { this.holdings = holdings; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Nested DTO for holdings
    public static class HoldingDetailDTO {
        private Long holdingId;
        private String assetName;
        private String assetType;
        private BigDecimal quantity;
        private BigDecimal purchasePrice;
        private BigDecimal currentPrice;
        private String currency;
        private LocalDate purchaseDate;
        private BigDecimal totalInvested;
        private BigDecimal currentValue;
        private BigDecimal gainLoss;
        private BigDecimal gainLossPercentage;
        private BigDecimal allocation;

        public HoldingDetailDTO() {}

        public HoldingDetailDTO(Long holdingId, String assetName, String assetType,
                              BigDecimal quantity, BigDecimal purchasePrice, BigDecimal currentPrice,
                              String currency, LocalDate purchaseDate) {
            this.holdingId = holdingId;
            this.assetName = assetName;
            this.assetType = assetType;
            this.quantity = quantity;
            this.purchasePrice = purchasePrice;
            this.currentPrice = currentPrice;
            this.currency = currency;
            this.purchaseDate = purchaseDate;
            this.totalInvested = purchasePrice.multiply(quantity);
            this.currentValue = currentPrice.multiply(quantity);
            this.gainLoss = this.currentValue.subtract(this.totalInvested);

            if (this.totalInvested.compareTo(BigDecimal.ZERO) != 0) {
                this.gainLossPercentage = this.gainLoss.divide(this.totalInvested, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            } else {
                this.gainLossPercentage = BigDecimal.ZERO;
            }
        }

        // Getters and Setters
        public Long getHoldingId() { return holdingId; }
        public void setHoldingId(Long holdingId) { this.holdingId = holdingId; }

        public String getAssetName() { return assetName; }
        public void setAssetName(String assetName) { this.assetName = assetName; }

        public String getAssetType() { return assetType; }
        public void setAssetType(String assetType) { this.assetType = assetType; }

        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

        public BigDecimal getPurchasePrice() { return purchasePrice; }
        public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }

        public BigDecimal getCurrentPrice() { return currentPrice; }
        public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public LocalDate getPurchaseDate() { return purchaseDate; }
        public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

        public BigDecimal getTotalInvested() { return totalInvested; }
        public void setTotalInvested(BigDecimal totalInvested) { this.totalInvested = totalInvested; }

        public BigDecimal getCurrentValue() { return currentValue; }
        public void setCurrentValue(BigDecimal currentValue) { this.currentValue = currentValue; }

        public BigDecimal getGainLoss() { return gainLoss; }
        public void setGainLoss(BigDecimal gainLoss) { this.gainLoss = gainLoss; }

        public BigDecimal getGainLossPercentage() { return gainLossPercentage; }
        public void setGainLossPercentage(BigDecimal gainLossPercentage) { this.gainLossPercentage = gainLossPercentage; }

        public BigDecimal getAllocation() { return allocation; }
        public void setAllocation(BigDecimal allocation) { this.allocation = allocation; }
    }
}
