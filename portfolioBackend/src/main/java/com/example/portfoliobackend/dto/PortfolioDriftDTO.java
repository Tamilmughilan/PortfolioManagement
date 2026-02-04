package com.example.portfoliobackend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PortfolioDriftDTO {
    private Long portfolioId;
    private String portfolioName;
    private String baseCurrency;
    private BigDecimal initialValue;
    private BigDecimal latestValue;
    private BigDecimal driftValue;
    private BigDecimal driftPercent;
    private LocalDate initialDate;
    private LocalDate latestDate;
    private String narrative;
    private List<TimelineEntry> timeline;

    public PortfolioDriftDTO() {
    }

    public PortfolioDriftDTO(
            Long portfolioId,
            String portfolioName,
            String baseCurrency,
            BigDecimal initialValue,
            BigDecimal latestValue,
            BigDecimal driftValue,
            BigDecimal driftPercent,
            LocalDate initialDate,
            LocalDate latestDate,
            String narrative,
            List<TimelineEntry> timeline
    ) {
        this.portfolioId = portfolioId;
        this.portfolioName = portfolioName;
        this.baseCurrency = baseCurrency;
        this.initialValue = initialValue;
        this.latestValue = latestValue;
        this.driftValue = driftValue;
        this.driftPercent = driftPercent;
        this.initialDate = initialDate;
        this.latestDate = latestDate;
        this.narrative = narrative;
        this.timeline = timeline;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public BigDecimal getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(BigDecimal initialValue) {
        this.initialValue = initialValue;
    }

    public BigDecimal getLatestValue() {
        return latestValue;
    }

    public void setLatestValue(BigDecimal latestValue) {
        this.latestValue = latestValue;
    }

    public BigDecimal getDriftValue() {
        return driftValue;
    }

    public void setDriftValue(BigDecimal driftValue) {
        this.driftValue = driftValue;
    }

    public BigDecimal getDriftPercent() {
        return driftPercent;
    }

    public void setDriftPercent(BigDecimal driftPercent) {
        this.driftPercent = driftPercent;
    }

    public LocalDate getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(LocalDate initialDate) {
        this.initialDate = initialDate;
    }

    public LocalDate getLatestDate() {
        return latestDate;
    }

    public void setLatestDate(LocalDate latestDate) {
        this.latestDate = latestDate;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public List<TimelineEntry> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<TimelineEntry> timeline) {
        this.timeline = timeline;
    }

    public static class TimelineEntry {
        private LocalDate date;
        private BigDecimal totalValue;
        private BigDecimal driftFromStart;
        private BigDecimal driftPercentFromStart;
        private String story;

        public TimelineEntry() {
        }

        public TimelineEntry(
                LocalDate date,
                BigDecimal totalValue,
                BigDecimal driftFromStart,
                BigDecimal driftPercentFromStart,
                String story
        ) {
            this.date = date;
            this.totalValue = totalValue;
            this.driftFromStart = driftFromStart;
            this.driftPercentFromStart = driftPercentFromStart;
            this.story = story;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public BigDecimal getTotalValue() {
            return totalValue;
        }

        public void setTotalValue(BigDecimal totalValue) {
            this.totalValue = totalValue;
        }

        public BigDecimal getDriftFromStart() {
            return driftFromStart;
        }

        public void setDriftFromStart(BigDecimal driftFromStart) {
            this.driftFromStart = driftFromStart;
        }

        public BigDecimal getDriftPercentFromStart() {
            return driftPercentFromStart;
        }

        public void setDriftPercentFromStart(BigDecimal driftPercentFromStart) {
            this.driftPercentFromStart = driftPercentFromStart;
        }

        public String getStory() {
            return story;
        }

        public void setStory(String story) {
            this.story = story;
        }
    }
}
