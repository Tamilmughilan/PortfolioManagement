package com.example.portfoliobackend.controller;

import com.example.portfoliobackend.service.AnalyticsService;
import com.example.portfoliobackend.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private PortfolioService portfolioService;

    @GetMapping("/portfolios/{portfolioId}/summary")
    public ResponseEntity<AnalyticsSummary> getSummary(@PathVariable Long portfolioId) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.notFound().build();
        }
        BigDecimal totalMarketValue = analyticsService.getTotalMarketValue(portfolioId);
        BigDecimal totalCost = analyticsService.getTotalCost(portfolioId);
        BigDecimal totalGainLoss = analyticsService.getTotalGainLoss(portfolioId);
        return ResponseEntity.ok(new AnalyticsSummary(totalMarketValue, totalCost, totalGainLoss));
    }

    @GetMapping("/portfolios/{portfolioId}/allocations")
    public ResponseEntity<Map<String, BigDecimal>> getAllocationValues(@PathVariable Long portfolioId) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(analyticsService.getAllocationValues(portfolioId));
    }

    @GetMapping("/portfolios/{portfolioId}/allocation-percentages")
    public ResponseEntity<Map<String, BigDecimal>> getAllocationPercentages(@PathVariable Long portfolioId) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(analyticsService.getAllocationPercentages(portfolioId));
    }

    @GetMapping("/portfolios/{portfolioId}/target-drift")
    public ResponseEntity<Map<String, BigDecimal>> getTargetDrift(@PathVariable Long portfolioId) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(analyticsService.getTargetDriftPercentages(portfolioId));
    }

    public static class AnalyticsSummary {
        private BigDecimal totalMarketValue;
        private BigDecimal totalCost;
        private BigDecimal totalGainLoss;

        public AnalyticsSummary(BigDecimal totalMarketValue, BigDecimal totalCost, BigDecimal totalGainLoss) {
            this.totalMarketValue = totalMarketValue;
            this.totalCost = totalCost;
            this.totalGainLoss = totalGainLoss;
        }

        public BigDecimal getTotalMarketValue() {
            return totalMarketValue;
        }

        public BigDecimal getTotalCost() {
            return totalCost;
        }

        public BigDecimal getTotalGainLoss() {
            return totalGainLoss;
        }
    }
}
