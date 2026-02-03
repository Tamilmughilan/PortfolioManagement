package com.example.portfoliobackend.controller;

import com.example.portfoliobackend.entity.Portfolio;
import com.example.portfoliobackend.service.AnalyticsService;
import com.example.portfoliobackend.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalyticsController.class)
@DisplayName("AnalyticsController Integration Tests")
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @MockBean
    private PortfolioService portfolioService;

    private Portfolio testPortfolio;

    @BeforeEach
    void setUp() {
        testPortfolio = new Portfolio();
        testPortfolio.setPortfolioId(1L);
        testPortfolio.setUserId(1L);
        testPortfolio.setPortfolioName("Test Portfolio");
    }

    @Test
    @DisplayName("GET /api/analytics/portfolios/{id}/summary - Should return analytics summary")
    void getSummary_WhenPortfolioExists_ShouldReturnSummary() throws Exception {
        when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
        when(analyticsService.getTotalMarketValue(1L)).thenReturn(new BigDecimal("10000.00"));
        when(analyticsService.getTotalCost(1L)).thenReturn(new BigDecimal("8000.00"));
        when(analyticsService.getTotalGainLoss(1L)).thenReturn(new BigDecimal("2000.00"));

        mockMvc.perform(get("/api/analytics/portfolios/1/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMarketValue", is(10000.00)))
                .andExpect(jsonPath("$.totalCost", is(8000.00)))
                .andExpect(jsonPath("$.totalGainLoss", is(2000.00)));

        verify(portfolioService, times(1)).getPortfolioById(1L);
        verify(analyticsService, times(1)).getTotalMarketValue(1L);
    }

    @Test
    @DisplayName("GET /api/analytics/portfolios/{id}/summary - Should return 404 when portfolio not exists")
    void getSummary_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
        when(portfolioService.getPortfolioById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/analytics/portfolios/999/summary"))
                .andExpect(status().isNotFound());

        verify(analyticsService, never()).getTotalMarketValue(anyLong());
    }

    @Test
    @DisplayName("GET /api/analytics/portfolios/{id}/allocations - Should return allocation values")
    void getAllocationValues_ShouldReturnAllocations() throws Exception {
        Map<String, BigDecimal> allocations = new HashMap<>();
        allocations.put("STOCK", new BigDecimal("6000.00"));
        allocations.put("BOND", new BigDecimal("4000.00"));

        when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
        when(analyticsService.getAllocationValues(1L)).thenReturn(allocations);

        mockMvc.perform(get("/api/analytics/portfolios/1/allocations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.STOCK", is(6000.00)))
                .andExpect(jsonPath("$.BOND", is(4000.00)));
    }

    @Test
    @DisplayName("GET /api/analytics/portfolios/{id}/allocations - Should return 404 when portfolio not exists")
    void getAllocationValues_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
        when(portfolioService.getPortfolioById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/analytics/portfolios/999/allocations"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/analytics/portfolios/{id}/allocation-percentages - Should return percentages")
    void getAllocationPercentages_ShouldReturnPercentages() throws Exception {
        Map<String, BigDecimal> percentages = new HashMap<>();
        percentages.put("STOCK", new BigDecimal("60.00"));
        percentages.put("BOND", new BigDecimal("40.00"));

        when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
        when(analyticsService.getAllocationPercentages(1L)).thenReturn(percentages);

        mockMvc.perform(get("/api/analytics/portfolios/1/allocation-percentages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.STOCK", is(60.00)))
                .andExpect(jsonPath("$.BOND", is(40.00)));
    }

    @Test
    @DisplayName("GET /api/analytics/portfolios/{id}/allocation-percentages - Should return 404 when not exists")
    void getAllocationPercentages_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
        when(portfolioService.getPortfolioById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/analytics/portfolios/999/allocation-percentages"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/analytics/portfolios/{id}/target-drift - Should return drift percentages")
    void getTargetDrift_ShouldReturnDrift() throws Exception {
        Map<String, BigDecimal> drift = new HashMap<>();
        drift.put("STOCK", new BigDecimal("-5.00")); // Under target
        drift.put("BOND", new BigDecimal("5.00")); // Over target

        when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
        when(analyticsService.getTargetDriftPercentages(1L)).thenReturn(drift);

        mockMvc.perform(get("/api/analytics/portfolios/1/target-drift"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.STOCK", is(-5.00)))
                .andExpect(jsonPath("$.BOND", is(5.00)));
    }

    @Test
    @DisplayName("GET /api/analytics/portfolios/{id}/target-drift - Should return 404 when not exists")
    void getTargetDrift_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
        when(portfolioService.getPortfolioById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/analytics/portfolios/999/target-drift"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/analytics/portfolios/{id}/summary - Should handle zero values")
    void getSummary_WithZeroValues_ShouldReturnZeros() throws Exception {
        when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
        when(analyticsService.getTotalMarketValue(1L)).thenReturn(BigDecimal.ZERO);
        when(analyticsService.getTotalCost(1L)).thenReturn(BigDecimal.ZERO);
        when(analyticsService.getTotalGainLoss(1L)).thenReturn(BigDecimal.ZERO);

        mockMvc.perform(get("/api/analytics/portfolios/1/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMarketValue", is(0)))
                .andExpect(jsonPath("$.totalCost", is(0)))
                .andExpect(jsonPath("$.totalGainLoss", is(0)));
    }

    @Test
    @DisplayName("GET /api/analytics/portfolios/{id}/allocations - Should return empty map when no holdings")
    void getAllocationValues_WhenNoHoldings_ShouldReturnEmptyMap() throws Exception {
        when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
        when(analyticsService.getAllocationValues(1L)).thenReturn(new HashMap<>());

        mockMvc.perform(get("/api/analytics/portfolios/1/allocations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", anEmptyMap()));
    }
}