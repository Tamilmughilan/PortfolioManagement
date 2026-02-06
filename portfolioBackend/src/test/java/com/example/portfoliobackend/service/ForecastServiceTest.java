package com.example.portfoliobackend.service;

import com.example.portfoliobackend.dto.GoalForecastDTO;
import com.example.portfoliobackend.dto.WhatIfRequestDTO;
import com.example.portfoliobackend.dto.WhatIfResponseDTO;
import com.example.portfoliobackend.entity.PortfolioGoal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ForecastService Unit Tests")
class ForecastServiceTest {

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private ForecastService forecastService;

    private PortfolioGoal testGoal;

    @BeforeEach
    void setUp() {
        testGoal = new PortfolioGoal();
        testGoal.setGoalId(1L);
        testGoal.setPortfolioId(1L);
        testGoal.setGoalName("Dream Home");
        testGoal.setTargetAmount(new BigDecimal("500000"));
        testGoal.setTargetDate(LocalDate.now().plusYears(5));
        testGoal.setExpectedAnnualReturn(new BigDecimal("8.0"));
    }

    @Nested
    @DisplayName("forecastGoal Tests")
    class ForecastGoalTests {

        @Test
        @DisplayName("Should forecast goal with expected return")
        void forecastGoal_WithExpectedReturn_ShouldCalculateCorrectly() {
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));

            GoalForecastDTO result = forecastService.forecastGoal(1L, testGoal);

            assertThat(result).isNotNull();
            assertThat(result.getGoalId()).isEqualTo(1L);
            assertThat(result.getPortfolioId()).isEqualTo(1L);
            assertThat(result.getCurrentValue()).isEqualByComparingTo(new BigDecimal("100000"));
            assertThat(result.getTargetAmount()).isEqualByComparingTo(new BigDecimal("500000"));
            assertThat(result.getExpectedAnnualReturn()).isEqualByComparingTo(new BigDecimal("8.0"));
            assertThat(result.getMonthsRemaining()).isGreaterThan(0);
            assertThat(result.getTrajectory()).isNotNull();
            assertThat(result.getNarrative()).isNotNull();
        }

        @Test
        @DisplayName("Should use default return when expected return is null")
        void forecastGoal_WithNullExpectedReturn_ShouldUseDefault() {
            testGoal.setExpectedAnnualReturn(null);
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));

            GoalForecastDTO result = forecastService.forecastGoal(1L, testGoal);

            assertThat(result.getExpectedAnnualReturn()).isEqualByComparingTo(new BigDecimal("8.0"));
        }

        @Test
        @DisplayName("Should handle zero current value")
        void forecastGoal_WithZeroCurrentValue_ShouldCalculateCorrectly() {
            when(portfolioService.calculateTotalValue(1L)).thenReturn(BigDecimal.ZERO);

            GoalForecastDTO result = forecastService.forecastGoal(1L, testGoal);

            assertThat(result.getCurrentValue()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(result.getRequiredMonthlyContribution()).isNotNull();
        }

        @Test
        @DisplayName("Should handle past target date")
        void forecastGoal_WithPastTargetDate_ShouldSetMonthsRemainingToZero() {
            testGoal.setTargetDate(LocalDate.now().minusMonths(6));
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));

            GoalForecastDTO result = forecastService.forecastGoal(1L, testGoal);

            assertThat(result.getMonthsRemaining()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should generate positive narrative when on track")
        void forecastGoal_WhenOnTrack_ShouldGeneratePositiveNarrative() {
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("400000"));
            testGoal.setTargetAmount(new BigDecimal("500000"));
            testGoal.setTargetDate(LocalDate.now().plusYears(1));

            GoalForecastDTO result = forecastService.forecastGoal(1L, testGoal);

            assertThat(result.getNarrative()).contains("on pace");
        }

        @Test
        @DisplayName("Should generate warning narrative when below target")
        void forecastGoal_WhenBelowTarget_ShouldGenerateWarningNarrative() {
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("10000"));
            testGoal.setTargetAmount(new BigDecimal("500000"));
            testGoal.setTargetDate(LocalDate.now().plusYears(1));

            GoalForecastDTO result = forecastService.forecastGoal(1L, testGoal);

            assertThat(result.getNarrative()).contains("below target");
        }

        @Test
        @DisplayName("Should generate trajectory points")
        void forecastGoal_ShouldGenerateTrajectoryPoints() {
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));
            testGoal.setTargetDate(LocalDate.now().plusMonths(12));

            GoalForecastDTO result = forecastService.forecastGoal(1L, testGoal);

            assertThat(result.getTrajectory()).isNotNull();
            assertThat(result.getTrajectory().size()).isGreaterThan(1);
            assertThat(result.getTrajectory().get(0).getValue()).isEqualByComparingTo(new BigDecimal("100000"));
        }
    }

    @Nested
    @DisplayName("runWhatIf Tests")
    class RunWhatIfTests {

        @Test
        @DisplayName("Should run what-if simulation with monthly contribution")
        void runWhatIf_WithMonthlyContribution_ShouldCalculateCorrectly() {
            WhatIfRequestDTO request = new WhatIfRequestDTO();
            request.setMonthlyContribution(new BigDecimal("1000"));
            request.setExpectedAnnualReturn(new BigDecimal("8.0"));
            request.setMonths(24);
            
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));

            WhatIfResponseDTO result = forecastService.runWhatIf(1L, request);

            assertThat(result).isNotNull();
            assertThat(result.getStartingValue()).isEqualByComparingTo(new BigDecimal("100000"));
            assertThat(result.getMonths()).isEqualTo(24);
            assertThat(result.getMonthlyContribution()).isEqualByComparingTo(new BigDecimal("1000"));
            assertThat(result.getProjectedValue()).isNotNull();
            assertThat(result.getTrajectory()).isNotNull();
            assertThat(result.getNarrative()).isNotNull();
            assertThat(result.getEndDate()).isNotNull();
        }

        @Test
        @DisplayName("Should use default return when expected return is null")
        void runWhatIf_WithNullExpectedReturn_ShouldUseDefault() {
            WhatIfRequestDTO request = new WhatIfRequestDTO();
            request.setMonthlyContribution(new BigDecimal("500"));
            request.setExpectedAnnualReturn(null);
            request.setMonths(12);
            
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));

            WhatIfResponseDTO result = forecastService.runWhatIf(1L, request);

            assertThat(result.getExpectedAnnualReturn()).isEqualByComparingTo(new BigDecimal("8.0"));
        }

        @Test
        @DisplayName("Should use zero contribution when monthly contribution is null")
        void runWhatIf_WithNullMonthlyContribution_ShouldUseZero() {
            WhatIfRequestDTO request = new WhatIfRequestDTO();
            request.setMonthlyContribution(null);
            request.setExpectedAnnualReturn(new BigDecimal("8.0"));
            request.setMonths(12);
            
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));

            WhatIfResponseDTO result = forecastService.runWhatIf(1L, request);

            assertThat(result.getMonthlyContribution()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should resolve months from target date when months is null")
        void runWhatIf_WithTargetDate_ShouldResolveMonths() {
            WhatIfRequestDTO request = new WhatIfRequestDTO();
            request.setMonthlyContribution(new BigDecimal("500"));
            request.setExpectedAnnualReturn(new BigDecimal("8.0"));
            request.setMonths(null);
            request.setTargetDate(LocalDate.now().plusMonths(18));
            
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));

            WhatIfResponseDTO result = forecastService.runWhatIf(1L, request);

            assertThat(result.getMonths()).isEqualTo(18);
        }

        @Test
        @DisplayName("Should use default 12 months when both months and target date are null")
        void runWhatIf_WithNoMonthsOrDate_ShouldUseDefault12Months() {
            WhatIfRequestDTO request = new WhatIfRequestDTO();
            request.setMonthlyContribution(new BigDecimal("500"));
            request.setExpectedAnnualReturn(new BigDecimal("8.0"));
            request.setMonths(null);
            request.setTargetDate(null);
            
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));

            WhatIfResponseDTO result = forecastService.runWhatIf(1L, request);

            assertThat(result.getMonths()).isEqualTo(12);
        }

        @Test
        @DisplayName("Should use months parameter when both are provided")
        void runWhatIf_WithBothMonthsAndDate_ShouldUseMonths() {
            WhatIfRequestDTO request = new WhatIfRequestDTO();
            request.setMonthlyContribution(new BigDecimal("500"));
            request.setExpectedAnnualReturn(new BigDecimal("8.0"));
            request.setMonths(24);
            request.setTargetDate(LocalDate.now().plusMonths(18));
            
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));

            WhatIfResponseDTO result = forecastService.runWhatIf(1L, request);

            assertThat(result.getMonths()).isEqualTo(24);
        }

        @Test
        @DisplayName("Should generate trajectory with contributions")
        void runWhatIf_ShouldGenerateTrajectoryWithContributions() {
            WhatIfRequestDTO request = new WhatIfRequestDTO();
            request.setMonthlyContribution(new BigDecimal("1000"));
            request.setExpectedAnnualReturn(new BigDecimal("8.0"));
            request.setMonths(6);
            
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));

            WhatIfResponseDTO result = forecastService.runWhatIf(1L, request);

            assertThat(result.getTrajectory()).isNotNull();
            assertThat(result.getTrajectory().size()).isGreaterThan(1);
            // First point should be current value
            assertThat(result.getTrajectory().get(0).getValue()).isEqualByComparingTo(new BigDecimal("100000"));
        }

        @Test
        @DisplayName("Should calculate end date correctly")
        void runWhatIf_ShouldCalculateEndDate() {
            WhatIfRequestDTO request = new WhatIfRequestDTO();
            request.setMonthlyContribution(new BigDecimal("500"));
            request.setExpectedAnnualReturn(new BigDecimal("8.0"));
            request.setMonths(12);
            
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));

            WhatIfResponseDTO result = forecastService.runWhatIf(1L, request);

            assertThat(result.getEndDate()).isEqualTo(LocalDate.now().plusMonths(12));
        }

        @Test
        @DisplayName("Should handle zero months")
        void runWhatIf_WithZeroMonths_ShouldHandleGracefully() {
            WhatIfRequestDTO request = new WhatIfRequestDTO();
            request.setMonthlyContribution(new BigDecimal("500"));
            request.setExpectedAnnualReturn(new BigDecimal("8.0"));
            request.setMonths(0);
            
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));

            WhatIfResponseDTO result = forecastService.runWhatIf(1L, request);

            assertThat(result.getMonths()).isEqualTo(0);
            assertThat(result.getStartingValue()).isEqualByComparingTo(new BigDecimal("100000"));
            assertThat(result.getProjectedValue()).isEqualByComparingTo(new BigDecimal("100000"));
        }

        @Test
        @DisplayName("Should handle negative months by using default")
        void runWhatIf_WithNegativeMonths_ShouldUseDefault() {
            WhatIfRequestDTO request = new WhatIfRequestDTO();
            request.setMonthlyContribution(new BigDecimal("500"));
            request.setExpectedAnnualReturn(new BigDecimal("8.0"));
            request.setMonths(-5);
            request.setTargetDate(LocalDate.now().plusMonths(12));
            
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));

            WhatIfResponseDTO result = forecastService.runWhatIf(1L, request);

            assertThat(result.getMonths()).isEqualTo(12);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle very large target amount")
        void forecastGoal_WithVeryLargeTarget_ShouldCalculateCorrectly() {
            testGoal.setTargetAmount(new BigDecimal("10000000"));
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));

            GoalForecastDTO result = forecastService.forecastGoal(1L, testGoal);

            assertThat(result.getRequiredMonthlyContribution()).isNotNull();
            assertThat(result.getRequiredMonthlyContribution().compareTo(BigDecimal.ZERO)).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should handle very high expected return")
        void forecastGoal_WithHighReturn_ShouldCalculateCorrectly() {
            testGoal.setExpectedAnnualReturn(new BigDecimal("20.0"));
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));

            GoalForecastDTO result = forecastService.forecastGoal(1L, testGoal);

            assertThat(result.getExpectedAnnualReturn()).isEqualByComparingTo(new BigDecimal("20.0"));
            assertThat(result.getProjectedValue()).isNotNull();
        }

        @Test
        @DisplayName("Should handle very low expected return")
        void forecastGoal_WithLowReturn_ShouldCalculateCorrectly() {
            testGoal.setExpectedAnnualReturn(new BigDecimal("2.0"));
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));

            GoalForecastDTO result = forecastService.forecastGoal(1L, testGoal);

            assertThat(result.getExpectedAnnualReturn()).isEqualByComparingTo(new BigDecimal("2.0"));
            assertThat(result.getProjectedValue()).isNotNull();
        }

        @Test
        @DisplayName("Should handle very long time horizon")
        void forecastGoal_WithLongTimeHorizon_ShouldCalculateCorrectly() {
            testGoal.setTargetDate(LocalDate.now().plusYears(30));
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));

            GoalForecastDTO result = forecastService.forecastGoal(1L, testGoal);

            assertThat(result.getMonthsRemaining()).isGreaterThan(300);
            assertThat(result.getTrajectory().size()).isGreaterThan(300);
        }

        @Test
        @DisplayName("Should handle zero expected return")
        void forecastGoal_WithZeroReturn_ShouldCalculateCorrectly() {
            testGoal.setExpectedAnnualReturn(BigDecimal.ZERO);
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));

            GoalForecastDTO result = forecastService.forecastGoal(1L, testGoal);

            assertThat(result.getExpectedAnnualReturn()).isEqualByComparingTo(BigDecimal.ZERO);
            // With zero return, projected value should be close to current value
            assertThat(result.getProjectedValue()).isNotNull();
        }
    }
}
