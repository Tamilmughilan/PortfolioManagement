package com.example.portfoliobackend.service;

import com.example.portfoliobackend.entity.Holding;
import com.example.portfoliobackend.entity.PortfolioTarget;
import com.example.portfoliobackend.repository.HoldingRepository;
import com.example.portfoliobackend.repository.PortfolioTargetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private PortfolioTargetRepository portfolioTargetRepository;

    public BigDecimal getTotalMarketValue(Long portfolioId) {
        return getHoldingsByPortfolio(portfolioId).stream()
                .map(this::holdingMarketValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCost(Long portfolioId) {
        return getHoldingsByPortfolio(portfolioId).stream()
                .map(this::holdingCostValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalGainLoss(Long portfolioId) {
        return getTotalMarketValue(portfolioId).subtract(getTotalCost(portfolioId));
    }

    public Map<String, BigDecimal> getAllocationValues(Long portfolioId) {
        Map<String, BigDecimal> allocations = new HashMap<>();
        for (Holding holding : getHoldingsByPortfolio(portfolioId)) {
            if (holding.getAssetType() == null) {
                continue;
            }
            allocations.merge(
                    holding.getAssetType(),
                    holdingMarketValue(holding),
                    BigDecimal::add
            );
        }
        return allocations;
    }

    public Map<String, BigDecimal> getAllocationPercentages(Long portfolioId) {
        BigDecimal totalValue = getTotalMarketValue(portfolioId);
        if (totalValue.compareTo(BigDecimal.ZERO) == 0) {
            return new HashMap<>();
        }

        return getAllocationValues(portfolioId).entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> percentageOf(entry.getValue(), totalValue)
                ));
    }

    public Map<String, BigDecimal> getTargetDriftPercentages(Long portfolioId) {
        Map<String, BigDecimal> allocationPercentages = getAllocationPercentages(portfolioId);
        Map<String, BigDecimal> drift = new HashMap<>();

        for (PortfolioTarget target : getTargetsByPortfolio(portfolioId)) {
            if (target.getAssetType() == null || target.getTargetPercentage() == null) {
                continue;
            }
            BigDecimal actual = allocationPercentages.getOrDefault(target.getAssetType(), BigDecimal.ZERO);
            drift.put(target.getAssetType(), actual.subtract(target.getTargetPercentage()));
        }
        return drift;
    }

    private List<Holding> getHoldingsByPortfolio(Long portfolioId) {
        return holdingRepository.findByPortfolioId(portfolioId);
    }

    private List<PortfolioTarget> getTargetsByPortfolio(Long portfolioId) {
        return portfolioTargetRepository.findByPortfolioId(portfolioId);
    }

    private BigDecimal holdingMarketValue(Holding holding) {
        if (holding.getQuantity() == null || holding.getCurrentPrice() == null) {
            return BigDecimal.ZERO;
        }
        return holding.getQuantity().multiply(holding.getCurrentPrice());
    }

    private BigDecimal holdingCostValue(Holding holding) {
        if (holding.getQuantity() == null || holding.getPurchasePrice() == null) {
            return BigDecimal.ZERO;
        }
        return holding.getQuantity().multiply(holding.getPurchasePrice());
    }

    private BigDecimal percentageOf(BigDecimal value, BigDecimal total) {
        return value.divide(total, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
