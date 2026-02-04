package com.example.portfoliobackend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class WhatIfResponseDTO {
    private BigDecimal startingValue;
    private BigDecimal projectedValue;
    private Integer months;
    private BigDecimal expectedAnnualReturn;
    private BigDecimal monthlyContribution;
    private List<GoalForecastDTO.ForecastPoint> trajectory;
    private String narrative;
    private LocalDate endDate;

    public WhatIfResponseDTO() {
    }

    public WhatIfResponseDTO(
            BigDecimal startingValue,
            BigDecimal projectedValue,
            Integer months,
            BigDecimal expectedAnnualReturn,
            BigDecimal monthlyContribution,
            List<GoalForecastDTO.ForecastPoint> trajectory,
            String narrative,
            LocalDate endDate
    ) {
        this.startingValue = startingValue;
        this.projectedValue = projectedValue;
        this.months = months;
        this.expectedAnnualReturn = expectedAnnualReturn;
        this.monthlyContribution = monthlyContribution;
        this.trajectory = trajectory;
        this.narrative = narrative;
        this.endDate = endDate;
    }

    public BigDecimal getStartingValue() {
        return startingValue;
    }

    public void setStartingValue(BigDecimal startingValue) {
        this.startingValue = startingValue;
    }

    public BigDecimal getProjectedValue() {
        return projectedValue;
    }

    public void setProjectedValue(BigDecimal projectedValue) {
        this.projectedValue = projectedValue;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public BigDecimal getExpectedAnnualReturn() {
        return expectedAnnualReturn;
    }

    public void setExpectedAnnualReturn(BigDecimal expectedAnnualReturn) {
        this.expectedAnnualReturn = expectedAnnualReturn;
    }

    public BigDecimal getMonthlyContribution() {
        return monthlyContribution;
    }

    public void setMonthlyContribution(BigDecimal monthlyContribution) {
        this.monthlyContribution = monthlyContribution;
    }

    public List<GoalForecastDTO.ForecastPoint> getTrajectory() {
        return trajectory;
    }

    public void setTrajectory(List<GoalForecastDTO.ForecastPoint> trajectory) {
        this.trajectory = trajectory;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
