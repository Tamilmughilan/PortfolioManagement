package com.example.portfoliobackend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TrendForecastDTO {
    private Long portfolioId;
    private List<Point> actual;
    private List<Point> forecast;
    private List<Point> movingAverage;
    private String narrative;

    public TrendForecastDTO() {
    }

    public TrendForecastDTO(Long portfolioId, List<Point> actual, List<Point> forecast, List<Point> movingAverage, String narrative) {
        this.portfolioId = portfolioId;
        this.actual = actual;
        this.forecast = forecast;
        this.movingAverage = movingAverage;
        this.narrative = narrative;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public List<Point> getActual() {
        return actual;
    }

    public void setActual(List<Point> actual) {
        this.actual = actual;
    }

    public List<Point> getForecast() {
        return forecast;
    }

    public void setForecast(List<Point> forecast) {
        this.forecast = forecast;
    }

    public List<Point> getMovingAverage() {
        return movingAverage;
    }

    public void setMovingAverage(List<Point> movingAverage) {
        this.movingAverage = movingAverage;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public static class Point {
        private LocalDate date;
        private BigDecimal value;

        public Point() {
        }

        public Point(LocalDate date, BigDecimal value) {
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