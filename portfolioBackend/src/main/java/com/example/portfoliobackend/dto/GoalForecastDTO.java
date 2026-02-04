package com.example.portfoliobackend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class GoalForecastDTO {
    private Long goalId;
    private Long portfolioId;
    private BigDecimal currentValue;
    private BigDecimal targetAmount;
    private LocalDate targetDate;
    private BigDecimal expectedAnnualReturn;
    private BigDecimal projectedValue;
    private BigDecimal requiredMonthlyContribution;
    private Integer monthsRemaining;
    private String narrative;
    private List<ForecastPoint> trajectory;

    public GoalForecastDTO() {
    }

    public GoalForecastDTO(
            Long goalId,
            Long portfolioId,
            BigDecimal currentValue,
            BigDecimal targetAmount,
            LocalDate targetDate,
            BigDecimal expectedAnnualReturn,
            BigDecimal projectedValue,
            BigDecimal requiredMonthlyContribution,
            Integer monthsRemaining,
            String narrative,
            List<ForecastPoint> trajectory
    ) {
        this.goalId = goalId;
        this.portfolioId = portfolioId;
        this.currentValue = currentValue;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.expectedAnnualReturn = expectedAnnualReturn;
        this.projectedValue = projectedValue;
        this.requiredMonthlyContribution = requiredMonthlyContribution;
        this.monthsRemaining = monthsRemaining;
        this.narrative = narrative;
        this.trajectory = trajectory;
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

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
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

    public BigDecimal getProjectedValue() {
        return projectedValue;
    }

    public void setProjectedValue(BigDecimal projectedValue) {
        this.projectedValue = projectedValue;
    }

    public BigDecimal getRequiredMonthlyContribution() {
        return requiredMonthlyContribution;
    }

    public void setRequiredMonthlyContribution(BigDecimal requiredMonthlyContribution) {
        this.requiredMonthlyContribution = requiredMonthlyContribution;
    }

    public Integer getMonthsRemaining() {
        return monthsRemaining;
    }

    public void setMonthsRemaining(Integer monthsRemaining) {
        this.monthsRemaining = monthsRemaining;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public List<ForecastPoint> getTrajectory() {
        return trajectory;
    }

    public void setTrajectory(List<ForecastPoint> trajectory) {
        this.trajectory = trajectory;
    }

    public static class ForecastPoint {
        private LocalDate date;
        private BigDecimal value;

        public ForecastPoint() {
        }

        public ForecastPoint(LocalDate date, BigDecimal value) {
            this.date = date;
            this.value = value;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }
    }
}
