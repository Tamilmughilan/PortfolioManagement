package com.example.portfoliobackend.service;

import com.example.portfoliobackend.entity.Holding;
import com.example.portfoliobackend.entity.PortfolioTarget;
import com.example.portfoliobackend.repository.HoldingRepository;
import com.example.portfoliobackend.repository.PortfolioTargetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnalyticsService Unit Tests")
class AnalyticsServiceTest {

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private PortfolioTargetRepository portfolioTargetRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private Holding stockHolding;
    private Holding bondHolding;
    private PortfolioTarget stockTarget;
    private PortfolioTarget bondTarget;

    @BeforeEach
    void setUp() {
        stockHolding = new Holding();
        stockHolding.setHoldingId(1L);
        stockHolding.setPortfolioId(1L);
        stockHolding.setAssetType("STOCK");
        stockHolding.setQuantity(new BigDecimal("10"));
        stockHolding.setPurchasePrice(new BigDecimal("100"));
        stockHolding.setCurrentPrice(new BigDecimal("150"));

        bondHolding = new Holding();
        bondHolding.setHoldingId(2L);
        bondHolding.setPortfolioId(1L);
        bondHolding.setAssetType("BOND");
        bondHolding.setQuantity(new BigDecimal("20"));
        bondHolding.setPurchasePrice(new BigDecimal("50"));
        bondHolding.setCurrentPrice(new BigDecimal("55"));

        stockTarget = new PortfolioTarget();
        stockTarget.setTargetId(1L);
        stockTarget.setPortfolioId(1L);
        stockTarget.setAssetType("STOCK");
        stockTarget.setTargetPercentage(new BigDecimal("60.00"));

        bondTarget = new PortfolioTarget();
        bondTarget.setTargetId(2L);
        bondTarget.setPortfolioId(1L);
        bondTarget.setAssetType("BOND");
        bondTarget.setTargetPercentage(new BigDecimal("40.00"));
    }

    @Nested
    @DisplayName("Market Value Tests")
    class MarketValueTests {

        @Test
        @DisplayName("Should calculate total market value correctly")
        void getTotalMarketValue_ShouldReturnCorrectValue() {
            List<Holding> holdings = Arrays.asList(stockHolding, bondHolding);
            when(holdingRepository.findByPortfolioId(1L)).thenReturn(holdings);

            BigDecimal result = analyticsService.getTotalMarketValue(1L);

            // Stock: 10 * 150 = 1500
            // Bond: 20 * 55 = 1100
            // Total: 2600
            assertThat(result).isEqualByComparingTo(new BigDecimal("2600"));
            verify(holdingRepository, times(1)).findByPortfolioId(1L);
        }

        @Test
        @DisplayName("Should return zero when no holdings")
        void getTotalMarketValue_WhenNoHoldings_ShouldReturnZero() {
            when(holdingRepository.findByPortfolioId(1L)).thenReturn(Collections.emptyList());

            BigDecimal result = analyticsService.getTotalMarketValue(1L);

            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should handle null quantity gracefully")
        void getTotalMarketValue_WhenNullQuantity_ShouldSkipHolding() {
            Holding nullQuantityHolding = new Holding();
            nullQuantityHolding.setCurrentPrice(new BigDecimal("100"));
            // quantity is null

            List<Holding> holdings = Arrays.asList(stockHolding, nullQuantityHolding);
            when(holdingRepository.findByPortfolioId(1L)).thenReturn(holdings);

            BigDecimal result = analyticsService.getTotalMarketValue(1L);

            // Only stockHolding: 10 * 150 = 1500
            assertThat(result).isEqualByComparingTo(new BigDecimal("1500"));
        }
    }

    @Nested
    @DisplayName("Cost Tests")
    class CostTests {

        @Test
        @DisplayName("Should calculate total cost correctly")
        void getTotalCost_ShouldReturnCorrectValue() {
            List<Holding> holdings = Arrays.asList(stockHolding, bondHolding);
            when(holdingRepository.findByPortfolioId(1L)).thenReturn(holdings);

            BigDecimal result = analyticsService.getTotalCost(1L);

            // Stock: 10 * 100 = 1000
            // Bond: 20 * 50 = 1000
            // Total: 2000
            assertThat(result).isEqualByComparingTo(new BigDecimal("2000"));
        }
    }

    @Nested
    @DisplayName("Gain/Loss Tests")
    class GainLossTests {

        @Test
        @DisplayName("Should calculate gain correctly when profitable")
        void getTotalGainLoss_WhenProfitable_ShouldReturnPositive() {
            List<Holding> holdings = Arrays.asList(stockHolding, bondHolding);
            when(holdingRepository.findByPortfolioId(1L)).thenReturn(holdings);

            BigDecimal result = analyticsService.getTotalGainLoss(1L);

            // Market Value: 2600
            // Cost: 2000
            // Gain: 600
            assertThat(result).isEqualByComparingTo(new BigDecimal("600"));
        }

        @Test
        @DisplayName("Should calculate loss correctly when not profitable")
        void getTotalGainLoss_WhenLoss_ShouldReturnNegative() {
            stockHolding.setCurrentPrice(new BigDecimal("80")); // Loss
            bondHolding.setCurrentPrice(new BigDecimal("40")); // Loss

            List<Holding> holdings = Arrays.asList(stockHolding, bondHolding);
            when(holdingRepository.findByPortfolioId(1L)).thenReturn(holdings);

            BigDecimal result = analyticsService.getTotalGainLoss(1L);

            // Market Value: 10*80 + 20*40 = 800 + 800 = 1600
            // Cost: 2000
            // Loss: -400
            assertThat(result).isEqualByComparingTo(new BigDecimal("-400"));
        }
    }

    @Nested
    @DisplayName("Allocation Tests")
    class AllocationTests {

        @Test
        @DisplayName("Should calculate allocation values by asset type")
        void getAllocationValues_ShouldReturnValuesByAssetType() {
            List<Holding> holdings = Arrays.asList(stockHolding, bondHolding);
            when(holdingRepository.findByPortfolioId(1L)).thenReturn(holdings);

            Map<String, BigDecimal> result = analyticsService.getAllocationValues(1L);

            assertThat(result).hasSize(2);
            assertThat(result.get("STOCK")).isEqualByComparingTo(new BigDecimal("1500"));
            assertThat(result.get("BOND")).isEqualByComparingTo(new BigDecimal("1100"));
        }

        @Test
        @DisplayName("Should calculate allocation percentages correctly")
        void getAllocationPercentages_ShouldReturnCorrectPercentages() {
            List<Holding> holdings = Arrays.asList(stockHolding, bondHolding);
            when(holdingRepository.findByPortfolioId(1L)).thenReturn(holdings);

            Map<String, BigDecimal> result = analyticsService.getAllocationPercentages(1L);

            assertThat(result).hasSize(2);
            // Stock: 1500/2600 * 100 = 57.69%
            // Bond: 1100/2600 * 100 = 42.31%
            assertThat(result.get("STOCK")).isEqualByComparingTo(new BigDecimal("57.69"));
            assertThat(result.get("BOND")).isEqualByComparingTo(new BigDecimal("42.31"));
        }

        @Test
        @DisplayName("Should return empty map when no holdings")
        void getAllocationPercentages_WhenNoHoldings_ShouldReturnEmptyMap() {
            when(holdingRepository.findByPortfolioId(1L)).thenReturn(Collections.emptyList());

            Map<String, BigDecimal> result = analyticsService.getAllocationPercentages(1L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Target Drift Tests")
    class TargetDriftTests {

        @Test
        @DisplayName("Should calculate target drift correctly")
        void getTargetDriftPercentages_ShouldReturnCorrectDrift() {
            List<Holding> holdings = Arrays.asList(stockHolding, bondHolding);
            List<PortfolioTarget> targets = Arrays.asList(stockTarget, bondTarget);
            
            when(holdingRepository.findByPortfolioId(1L)).thenReturn(holdings);
            when(portfolioTargetRepository.findByPortfolioId(1L)).thenReturn(targets);

            Map<String, BigDecimal> result = analyticsService.getTargetDriftPercentages(1L);

            assertThat(result).hasSize(2);
            // Stock actual: 57.69%, target: 60% -> drift: -2.31
            // Bond actual: 42.31%, target: 40% -> drift: +2.31
            assertThat(result.get("STOCK")).isEqualByComparingTo(new BigDecimal("-2.31"));
            assertThat(result.get("BOND")).isEqualByComparingTo(new BigDecimal("2.31"));
        }

        @Test
        @DisplayName("Should return zero drift for missing asset types")
        void getTargetDriftPercentages_WhenAssetTypeMissing_ShouldReturnNegativeTarget() {
            // Only stock holdings, but bond target exists
            List<Holding> holdings = Arrays.asList(stockHolding);
            List<PortfolioTarget> targets = Arrays.asList(stockTarget, bondTarget);
            
            when(holdingRepository.findByPortfolioId(1L)).thenReturn(holdings);
            when(portfolioTargetRepository.findByPortfolioId(1L)).thenReturn(targets);

            Map<String, BigDecimal> result = analyticsService.getTargetDriftPercentages(1L);

            // Stock: 100% actual, 60% target -> drift: +40
            // Bond: 0% actual, 40% target -> drift: -40
            assertThat(result.get("STOCK")).isEqualByComparingTo(new BigDecimal("40.00"));
            assertThat(result.get("BOND")).isEqualByComparingTo(new BigDecimal("-40.00"));
        }
        
        @Test
        @DisplayName("Should return empty map when no targets")
        void getTargetDriftPercentages_WhenNoTargets_ShouldReturnEmptyMap() {
            List<Holding> holdings = Arrays.asList(stockHolding, bondHolding);
            when(holdingRepository.findByPortfolioId(1L)).thenReturn(holdings);
            when(portfolioTargetRepository.findByPortfolioId(1L)).thenReturn(Collections.emptyList());

            Map<String, BigDecimal> result = analyticsService.getTargetDriftPercentages(1L);

            assertThat(result).isEmpty();
        }
        
        @Test
        @DisplayName("Should handle null current price gracefully")
        void getTotalMarketValue_WhenNullCurrentPrice_ShouldSkipHolding() {
            Holding nullPriceHolding = new Holding();
            nullPriceHolding.setQuantity(new BigDecimal("10"));
            // currentPrice is null

            List<Holding> holdings = Arrays.asList(stockHolding, nullPriceHolding);
            when(holdingRepository.findByPortfolioId(1L)).thenReturn(holdings);

            BigDecimal result = analyticsService.getTotalMarketValue(1L);

            // Only stockHolding: 10 * 150 = 1500
            assertThat(result).isEqualByComparingTo(new BigDecimal("1500"));
        }
        
        @Test
        @DisplayName("Should handle null purchase price gracefully")
        void getTotalCost_WhenNullPurchasePrice_ShouldSkipHolding() {
            Holding nullPriceHolding = new Holding();
            nullPriceHolding.setQuantity(new BigDecimal("10"));
            // purchasePrice is null

            List<Holding> holdings = Arrays.asList(stockHolding, nullPriceHolding);
            when(holdingRepository.findByPortfolioId(1L)).thenReturn(holdings);

            BigDecimal result = analyticsService.getTotalCost(1L);

            // Only stockHolding: 10 * 100 = 1000
            assertThat(result).isEqualByComparingTo(new BigDecimal("1000"));
        }
    }
}
