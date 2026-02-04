package com.example.portfoliobackend.controller;

import com.example.portfoliobackend.entity.Portfolio;
import com.example.portfoliobackend.service.AnalyticsService;
import com.example.portfoliobackend.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalyticsController.class)
@ActiveProfiles("test")
@DisplayName("AnalyticsController Integration Tests")
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalyticsService analyticsService;

    @MockitoBean
    private PortfolioService portfolioService;

    private Portfolio testPortfolio;
    private Portfolio testPortfolio2;

    @BeforeEach
    void setUp() {
        testPortfolio = new Portfolio();
        testPortfolio.setPortfolioId(1L);
        testPortfolio.setUserId(1L);
        testPortfolio.setPortfolioName("Test Portfolio");
        testPortfolio.setBaseCurrency("USD");

        testPortfolio2 = new Portfolio();
        testPortfolio2.setPortfolioId(2L);
        testPortfolio2.setUserId(1L);
        testPortfolio2.setPortfolioName("Second Portfolio");
        testPortfolio2.setBaseCurrency("INR");
    }

    @Nested
    @DisplayName("GET /api/analytics/portfolios/{id}/summary Tests")
    class SummaryTests {

        @Test
        @DisplayName("Should return analytics summary when portfolio exists")
        void getSummary_WhenPortfolioExists_ShouldReturnSummary() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(analyticsService.getTotalMarketValue(1L)).thenReturn(new BigDecimal("10000.00"));
            when(analyticsService.getTotalCost(1L)).thenReturn(new BigDecimal("8000.00"));
            when(analyticsService.getTotalGainLoss(1L)).thenReturn(new BigDecimal("2000.00"));

            mockMvc.perform(get("/api/analytics/portfolios/1/summary"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.totalMarketValue", closeTo(10000.00, 0.01)))
                    .andExpect(jsonPath("$.totalCost", closeTo(8000.00, 0.01)))
                    .andExpect(jsonPath("$.totalGainLoss", closeTo(2000.00, 0.01)));

            verify(portfolioService, times(1)).getPortfolioById(1L);
            verify(analyticsService, times(1)).getTotalMarketValue(1L);
            verify(analyticsService, times(1)).getTotalCost(1L);
            verify(analyticsService, times(1)).getTotalGainLoss(1L);
        }

        @Test
        @DisplayName("Should return 404 when portfolio not exists")
        void getSummary_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(get("/api/analytics/portfolios/999/summary"))
                    .andExpect(status().isNotFound());

            verify(portfolioService, times(1)).getPortfolioById(999L);
            verify(analyticsService, never()).getTotalMarketValue(anyLong());
            verify(analyticsService, never()).getTotalCost(anyLong());
            verify(analyticsService, never()).getTotalGainLoss(anyLong());
        }

        @Test
        @DisplayName("Should handle zero values")
        void getSummary_WithZeroValues_ShouldReturnZeros() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(analyticsService.getTotalMarketValue(1L)).thenReturn(BigDecimal.ZERO);
            when(analyticsService.getTotalCost(1L)).thenReturn(BigDecimal.ZERO);
            when(analyticsService.getTotalGainLoss(1L)).thenReturn(BigDecimal.ZERO);

            mockMvc.perform(get("/api/analytics/portfolios/1/summary"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalMarketValue", closeTo(0.0, 0.01)))
                    .andExpect(jsonPath("$.totalCost", closeTo(0.0, 0.01)))
                    .andExpect(jsonPath("$.totalGainLoss", closeTo(0.0, 0.01)));
        }

        @Test
        @DisplayName("Should handle negative gain/loss")
        void getSummary_WithNegativeGainLoss_ShouldReturnNegativeValue() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(analyticsService.getTotalMarketValue(1L)).thenReturn(new BigDecimal("8000.00"));
            when(analyticsService.getTotalCost(1L)).thenReturn(new BigDecimal("10000.00"));
            when(analyticsService.getTotalGainLoss(1L)).thenReturn(new BigDecimal("-2000.00"));

            mockMvc.perform(get("/api/analytics/portfolios/1/summary"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalMarketValue", closeTo(8000.00, 0.01)))
                    .andExpect(jsonPath("$.totalCost", closeTo(10000.00, 0.01)))
                    .andExpect(jsonPath("$.totalGainLoss", closeTo(-2000.00, 0.01)));
        }

        @Test
        @DisplayName("Should handle large values")
        void getSummary_WithLargeValues_ShouldReturnCorrectValues() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(analyticsService.getTotalMarketValue(1L)).thenReturn(new BigDecimal("1000000.00"));
            when(analyticsService.getTotalCost(1L)).thenReturn(new BigDecimal("500000.00"));
            when(analyticsService.getTotalGainLoss(1L)).thenReturn(new BigDecimal("500000.00"));

            mockMvc.perform(get("/api/analytics/portfolios/1/summary"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalMarketValue", closeTo(1000000.00, 0.01)))
                    .andExpect(jsonPath("$.totalCost", closeTo(500000.00, 0.01)))
                    .andExpect(jsonPath("$.totalGainLoss", closeTo(500000.00, 0.01)));
        }

        @Test
        @DisplayName("Should return summary for different portfolio")
        void getSummary_ForDifferentPortfolio_ShouldReturnCorrectSummary() throws Exception {
            when(portfolioService.getPortfolioById(2L)).thenReturn(testPortfolio2);
            when(analyticsService.getTotalMarketValue(2L)).thenReturn(new BigDecimal("5000.00"));
            when(analyticsService.getTotalCost(2L)).thenReturn(new BigDecimal("4000.00"));
            when(analyticsService.getTotalGainLoss(2L)).thenReturn(new BigDecimal("1000.00"));

            mockMvc.perform(get("/api/analytics/portfolios/2/summary"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalMarketValue", closeTo(5000.00, 0.01)));

            verify(portfolioService, times(1)).getPortfolioById(2L);
            verify(analyticsService, times(1)).getTotalMarketValue(2L);
        }
    }

    @Nested
    @DisplayName("GET /api/analytics/portfolios/{id}/allocations Tests")
    class AllocationValuesTests {

        @Test
        @DisplayName("Should return allocation values")
        void getAllocationValues_ShouldReturnAllocations() throws Exception {
            Map<String, BigDecimal> allocations = new HashMap<>();
            allocations.put("STOCK", new BigDecimal("6000.00"));
            allocations.put("BOND", new BigDecimal("4000.00"));

            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(analyticsService.getAllocationValues(1L)).thenReturn(allocations);

            mockMvc.perform(get("/api/analytics/portfolios/1/allocations"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.STOCK", closeTo(6000.00, 0.01)))
                    .andExpect(jsonPath("$.BOND", closeTo(4000.00, 0.01)));

            verify(portfolioService, times(1)).getPortfolioById(1L);
            verify(analyticsService, times(1)).getAllocationValues(1L);
        }

        @Test
        @DisplayName("Should return 404 when portfolio not exists")
        void getAllocationValues_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(get("/api/analytics/portfolios/999/allocations"))
                    .andExpect(status().isNotFound());

            verify(analyticsService, never()).getAllocationValues(anyLong());
        }

        @Test
        @DisplayName("Should return empty map when no holdings")
        void getAllocationValues_WhenNoHoldings_ShouldReturnEmptyMap() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(analyticsService.getAllocationValues(1L)).thenReturn(new HashMap<>());

            mockMvc.perform(get("/api/analytics/portfolios/1/allocations"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", anEmptyMap()));
        }

        @Test
        @DisplayName("Should return multiple asset types")
        void getAllocationValues_WithMultipleAssetTypes_ShouldReturnAll() throws Exception {
            Map<String, BigDecimal> allocations = new HashMap<>();
            allocations.put("STOCK", new BigDecimal("5000.00"));
            allocations.put("BOND", new BigDecimal("3000.00"));
            allocations.put("CASH", new BigDecimal("2000.00"));

            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(analyticsService.getAllocationValues(1L)).thenReturn(allocations);

            mockMvc.perform(get("/api/analytics/portfolios/1/allocations"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", aMapWithSize(3)))
                    .andExpect(jsonPath("$.STOCK", closeTo(5000.00, 0.01)))
                    .andExpect(jsonPath("$.BOND", closeTo(3000.00, 0.01)))
                    .andExpect(jsonPath("$.CASH", closeTo(2000.00, 0.01)));
        }

        @Test
        @DisplayName("Should handle single asset type")
        void getAllocationValues_WithSingleAssetType_ShouldReturnOne() throws Exception {
            Map<String, BigDecimal> allocations = new HashMap<>();
            allocations.put("STOCK", new BigDecimal("10000.00"));

            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(analyticsService.getAllocationValues(1L)).thenReturn(allocations);

            mockMvc.perform(get("/api/analytics/portfolios/1/allocations"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", aMapWithSize(1)))
                    .andExpect(jsonPath("$.STOCK", closeTo(10000.00, 0.01)));
        }
    }

    @Nested
    @DisplayName("GET /api/analytics/portfolios/{id}/allocation-percentages Tests")
    class AllocationPercentagesTests {

        @Test
        @DisplayName("Should return percentages")
        void getAllocationPercentages_ShouldReturnPercentages() throws Exception {
            Map<String, BigDecimal> percentages = new HashMap<>();
            percentages.put("STOCK", new BigDecimal("60.00"));
            percentages.put("BOND", new BigDecimal("40.00"));

            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(analyticsService.getAllocationPercentages(1L)).thenReturn(percentages);

            mockMvc.perform(get("/api/analytics/portfolios/1/allocation-percentages"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.STOCK", closeTo(60.00, 0.01)))
                    .andExpect(jsonPath("$.BOND", closeTo(40.00, 0.01)));

            verify(portfolioService, times(1)).getPortfolioById(1L);
            verify(analyticsService, times(1)).getAllocationPercentages(1L);
        }

        @Test
        @DisplayName("Should return 404 when portfolio not exists")
        void getAllocationPercentages_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(get("/api/analytics/portfolios/999/allocation-percentages"))
                    .andExpect(status().isNotFound());

            verify(analyticsService, never()).getAllocationPercentages(anyLong());
        }

        @Test
        @DisplayName("Should return empty map when no holdings")
        void getAllocationPercentages_WhenNoHoldings_ShouldReturnEmptyMap() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(analyticsService.getAllocationPercentages(1L)).thenReturn(new HashMap<>());

            mockMvc.perform(get("/api/analytics/portfolios/1/allocation-percentages"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", anEmptyMap()));
        }

        @Test
        @DisplayName("Should handle 100% single allocation")
        void getAllocationPercentages_SingleAsset_ShouldReturn100Percent() throws Exception {
            Map<String, BigDecimal> percentages = new HashMap<>();
            percentages.put("STOCK", new BigDecimal("100.00"));

            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(analyticsService.getAllocationPercentages(1L)).thenReturn(percentages);

            mockMvc.perform(get("/api/analytics/portfolios/1/allocation-percentages"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.STOCK", closeTo(100.00, 0.01)));
        }

        @Test
        @DisplayName("Should handle fractional percentages")
        void getAllocationPercentages_FractionalValues_ShouldReturnCorrect() throws Exception {
            Map<String, BigDecimal> percentages = new HashMap<>();
            percentages.put("STOCK", new BigDecimal("33.33"));
            percentages.put("BOND", new BigDecimal("33.33"));
            percentages.put("CASH", new BigDecimal("33.34"));

            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(analyticsService.getAllocationPercentages(1L)).thenReturn(percentages);

            mockMvc.perform(get("/api/analytics/portfolios/1/allocation-percentages"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.STOCK", closeTo(33.33, 0.01)))
                    .andExpect(jsonPath("$.BOND", closeTo(33.33, 0.01)))
                    .andExpect(jsonPath("$.CASH", closeTo(33.34, 0.01)));
        }
    }

    @Nested
    @DisplayName("GET /api/analytics/portfolios/{id}/target-drift Tests")
    class TargetDriftTests {

        @Test
        @DisplayName("Should return drift percentages")
        void getTargetDrift_ShouldReturnDrift() throws Exception {
            Map<String, BigDecimal> drift = new HashMap<>();
            drift.put("STOCK", new BigDecimal("-5.00"));
            drift.put("BOND", new BigDecimal("5.00"));

            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(analyticsService.getTargetDriftPercentages(1L)).thenReturn(drift);

            mockMvc.perform(get("/api/analytics/portfolios/1/target-drift"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.STOCK", closeTo(-5.00, 0.01)))
                    .andExpect(jsonPath("$.BOND", closeTo(5.00, 0.01)));

            verify(portfolioService, times(1)).getPortfolioById(1L);
            verify(analyticsService, times(1)).getTargetDriftPercentages(1L);
        }

        @Test
        @DisplayName("Should return 404 when portfolio not exists")
        void getTargetDrift_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(get("/api/analytics/portfolios/999/target-drift"))
                    .andExpect(status().isNotFound());

            verify(analyticsService, never()).getTargetDriftPercentages(anyLong());
        }

        @Test
        @DisplayName("Should return empty map when no targets")
        void getTargetDrift_WhenNoTargets_ShouldReturnEmptyMap() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(analyticsService.getTargetDriftPercentages(1L)).thenReturn(new HashMap<>());

            mockMvc.perform(get("/api/analytics/portfolios/1/target-drift"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", anEmptyMap()));
        }

        @Test
        @DisplayName("Should handle zero drift")
        void getTargetDrift_WhenOnTarget_ShouldReturnZeroDrift() throws Exception {
            Map<String, BigDecimal> drift = new HashMap<>();
            drift.put("STOCK", BigDecimal.ZERO);
            drift.put("BOND", BigDecimal.ZERO);

            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(analyticsService.getTargetDriftPercentages(1L)).thenReturn(drift);

            mockMvc.perform(get("/api/analytics/portfolios/1/target-drift"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.STOCK", closeTo(0.0, 0.01)))
                    .andExpect(jsonPath("$.BOND", closeTo(0.0, 0.01)));
        }

        @Test
        @DisplayName("Should handle large negative drift")
        void getTargetDrift_LargeNegativeDrift_ShouldReturnCorrect() throws Exception {
            Map<String, BigDecimal> drift = new HashMap<>();
            drift.put("STOCK", new BigDecimal("-30.00"));

            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(analyticsService.getTargetDriftPercentages(1L)).thenReturn(drift);

            mockMvc.perform(get("/api/analytics/portfolios/1/target-drift"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.STOCK", closeTo(-30.00, 0.01)));
        }

        @Test
        @DisplayName("Should handle large positive drift")
        void getTargetDrift_LargePositiveDrift_ShouldReturnCorrect() throws Exception {
            Map<String, BigDecimal> drift = new HashMap<>();
            drift.put("STOCK", new BigDecimal("25.00"));

            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(analyticsService.getTargetDriftPercentages(1L)).thenReturn(drift);

            mockMvc.perform(get("/api/analytics/portfolios/1/target-drift"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.STOCK", closeTo(25.00, 0.01)));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle portfolio ID 0")
        void getAnalytics_WithZeroId_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(0L)).thenReturn(null);

            mockMvc.perform(get("/api/analytics/portfolios/0/summary"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should handle very small decimal values in summary")
        void getSummary_WithSmallDecimalValues_ShouldReturnCorrect() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(analyticsService.getTotalMarketValue(1L)).thenReturn(new BigDecimal("0.01"));
            when(analyticsService.getTotalCost(1L)).thenReturn(new BigDecimal("0.02"));
            when(analyticsService.getTotalGainLoss(1L)).thenReturn(new BigDecimal("-0.01"));

            mockMvc.perform(get("/api/analytics/portfolios/1/summary"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalMarketValue", closeTo(0.01, 0.001)))
                    .andExpect(jsonPath("$.totalCost", closeTo(0.02, 0.001)))
                    .andExpect(jsonPath("$.totalGainLoss", closeTo(-0.01, 0.001)));
        }
    }
}
