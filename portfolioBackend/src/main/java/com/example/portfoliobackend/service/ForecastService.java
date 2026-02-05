package com.example.portfoliobackend.service;

import com.example.portfoliobackend.dto.GoalForecastDTO;
import com.example.portfoliobackend.dto.TrendForecastDTO;
import com.example.portfoliobackend.dto.WhatIfRequestDTO;
import com.example.portfoliobackend.dto.WhatIfResponseDTO;
import com.example.portfoliobackend.entity.PortfolioSnapshot;
import com.example.portfoliobackend.entity.PortfolioGoal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ForecastService {

    private static final BigDecimal MONTHS_IN_YEAR = new BigDecimal("12");

    @Autowired
    private PortfolioService portfolioService;

    public GoalForecastDTO forecastGoal(Long portfolioId, PortfolioGoal goal) {
        BigDecimal currentValue = portfolioService.calculateTotalValue(portfolioId);
        LocalDate targetDate = goal.getTargetDate();
        int monthsRemaining = (int) Math.max(0, ChronoUnit.MONTHS.between(LocalDate.now(), targetDate));

        BigDecimal expectedAnnualReturn = goal.getExpectedAnnualReturn() != null
                ? goal.getExpectedAnnualReturn()
                : new BigDecimal("8.0");

        BigDecimal monthlyRate = expectedAnnualReturn
                .divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP)
                .divide(MONTHS_IN_YEAR, 8, RoundingMode.HALF_UP);

        BigDecimal projectedValue = compound(currentValue, monthlyRate, monthsRemaining, BigDecimal.ZERO);
        BigDecimal requiredMonthly = requiredMonthlyContribution(
                currentValue,
                goal.getTargetAmount(),
                monthlyRate,
                monthsRemaining
        );

        List<GoalForecastDTO.ForecastPoint> trajectory = buildTrajectory(
                currentValue,
                monthlyRate,
                monthsRemaining,
                BigDecimal.ZERO
        );

        String narrative = projectedValue.compareTo(goal.getTargetAmount()) >= 0
                ? "On current trajectory, you are on pace to reach your goal."
                : "You are below target trajectory. Consider increasing monthly contributions.";

        return new GoalForecastDTO(
                goal.getGoalId(),
                portfolioId,
                currentValue,
                goal.getTargetAmount(),
                goal.getTargetDate(),
                expectedAnnualReturn,
                projectedValue,
                requiredMonthly,
                monthsRemaining,
                narrative,
                trajectory
        );
    }

    public WhatIfResponseDTO runWhatIf(Long portfolioId, WhatIfRequestDTO request) {
        BigDecimal currentValue = portfolioService.calculateTotalValue(portfolioId);
        int months = resolveMonths(request.getMonths(), request.getTargetDate());
        BigDecimal expectedAnnualReturn = request.getExpectedAnnualReturn() != null
                ? request.getExpectedAnnualReturn()
                : new BigDecimal("8.0");
        BigDecimal monthlyContribution = request.getMonthlyContribution() != null
                ? request.getMonthlyContribution()
                : BigDecimal.ZERO;

        BigDecimal monthlyRate = expectedAnnualReturn
                .divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP)
                .divide(MONTHS_IN_YEAR, 8, RoundingMode.HALF_UP);

        BigDecimal projectedValue = compound(currentValue, monthlyRate, months, monthlyContribution);
        List<GoalForecastDTO.ForecastPoint> trajectory = buildTrajectory(
                currentValue,
                monthlyRate,
                months,
                monthlyContribution
        );

        LocalDate endDate = LocalDate.now().plusMonths(months);
        String narrative = "With a monthly contribution of " + monthlyContribution
                + ", your portfolio could reach " + projectedValue.setScale(2, RoundingMode.HALF_UP)
                + " by " + endDate + ".";

        return new WhatIfResponseDTO(
                currentValue,
                projectedValue,
                months,
                expectedAnnualReturn,
                monthlyContribution,
                trajectory,
                narrative,
                endDate
        );
    }

    public TrendForecastDTO forecastTrend(Long portfolioId, int monthsAhead) {
        List<PortfolioSnapshot> snapshots = portfolioService.getSnapshotsByPortfolioIdAsc(portfolioId);
        if (snapshots == null || snapshots.isEmpty()) {
            return new TrendForecastDTO(portfolioId, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                    "Not enough snapshot data to forecast.");
        }

        List<TrendForecastDTO.Point> actual = snapshots.stream()
                .map(s -> new TrendForecastDTO.Point(s.getSnapshotDate(), s.getTotalValue()))
                .collect(Collectors.toList());

        List<TrendForecastDTO.Point> movingAverage = buildMovingAverage(actual, 3);

        int n = actual.size();
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumXX = 0;
        for (int i = 0; i < n; i++) {
            double x = i;
            double y = actual.get(i).getValue().doubleValue();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumXX += x * x;
        }
        double slope = (n * sumXY - sumX * sumY) / Math.max(1.0, (n * sumXX - sumX * sumX));
        double intercept = (sumY - slope * sumX) / n;

        List<TrendForecastDTO.Point> forecast = new ArrayList<>();
        LocalDate lastDate = actual.get(n - 1).getDate();
        for (int i = 1; i <= monthsAhead; i++) {
            double y = slope * (n - 1 + i) + intercept;
            forecast.add(new TrendForecastDTO.Point(lastDate.plusMonths(i), BigDecimal.valueOf(y).setScale(2, RoundingMode.HALF_UP)));
        }

        String narrative = slope >= 0
                ? "Trend indicates gradual growth based on your snapshot history."
                : "Trend indicates a downward drift based on your snapshot history.";

        return new TrendForecastDTO(portfolioId, actual, forecast, movingAverage, narrative);
    }

    private List<TrendForecastDTO.Point> buildMovingAverage(List<TrendForecastDTO.Point> points, int window) {
        List<TrendForecastDTO.Point> averages = new ArrayList<>();
        if (points == null || points.isEmpty()) {
            return averages;
        }
        int size = points.size();
        for (int i = 0; i < size; i++) {
            int start = Math.max(0, i - window + 1);
            BigDecimal sum = BigDecimal.ZERO;
            int count = 0;
            for (int j = start; j <= i; j++) {
                sum = sum.add(points.get(j).getValue());
                count++;
            }
            BigDecimal avg = sum.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
            averages.add(new TrendForecastDTO.Point(points.get(i).getDate(), avg));
        }
        return averages;
    }

    private int resolveMonths(Integer months, LocalDate targetDate) {
        if (months != null && months > 0) {
            return months;
        }
        if (targetDate != null) {
            return (int) Math.max(0, ChronoUnit.MONTHS.between(LocalDate.now(), targetDate));
        }
        return 12;
    }

    private BigDecimal compound(BigDecimal principal, BigDecimal monthlyRate, int months, BigDecimal contribution) {
        BigDecimal value = principal;
        for (int i = 0; i < months; i++) {
            value = value.multiply(BigDecimal.ONE.add(monthlyRate)).add(contribution);
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal requiredMonthlyContribution(
            BigDecimal principal,
            BigDecimal target,
            BigDecimal monthlyRate,
            int months
    ) {
        if (months <= 0) {
            return target.subtract(principal).max(BigDecimal.ZERO);
        }
        BigDecimal futureValue = compound(principal, monthlyRate, months, BigDecimal.ZERO);
        BigDecimal gap = target.subtract(futureValue);
        if (gap.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal factor = BigDecimal.ZERO;
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal pow = BigDecimal.ONE;
        for (int i = 0; i < months; i++) {
            pow = pow.multiply(onePlusRate);
            factor = factor.add(pow);
        }
        if (factor.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return gap.divide(factor, 2, RoundingMode.HALF_UP);
    }

    private List<GoalForecastDTO.ForecastPoint> buildTrajectory(
            BigDecimal principal,
            BigDecimal monthlyRate,
            int months,
            BigDecimal contribution
    ) {
        List<GoalForecastDTO.ForecastPoint> points = new ArrayList<>();
        BigDecimal value = principal;
        LocalDate date = LocalDate.now();
        points.add(new GoalForecastDTO.ForecastPoint(date, value.setScale(2, RoundingMode.HALF_UP)));
        for (int i = 0; i < months; i++) {
            value = value.multiply(BigDecimal.ONE.add(monthlyRate)).add(contribution);
            date = date.plusMonths(1);
            points.add(new GoalForecastDTO.ForecastPoint(date, value.setScale(2, RoundingMode.HALF_UP)));
        }
        return points;
    }
}