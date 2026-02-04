package com.example.portfoliobackend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PortfolioGoalDTO {
    private Long goalId;
    private Long portfolioId;
    private String goalName;
    private BigDecimal targetAmount;
    private LocalDate targetDate;
    private BigDecimal expectedAnnualReturn;
    private LocalDateTime createdAt;

    public PortfolioGoalDTO() {
    }

    public PortfolioGoalDTO(
            Long goalId,
            Long portfolioId,
            String goalName,
            BigDecimal targetAmount,
            LocalDate targetDate,
            BigDecimal expectedAnnualReturn,
            LocalDateTime createdAt
    ) {
        this.goalId = goalId;
        this.portfolioId = portfolioId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.expectedAnnualReturn = expectedAnnualReturn;
        this.createdAt = createdAt;
    }

    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public BigDecimal getExpectedAnnualReturn() {
        return expectedAnnualReturn;
    }

    public void setExpectedAnnualReturn(BigDecimal expectedAnnualReturn) {
        this.expectedAnnualReturn = expectedAnnualReturn;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
