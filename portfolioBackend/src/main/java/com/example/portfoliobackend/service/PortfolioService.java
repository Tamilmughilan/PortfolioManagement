package com.example.portfoliobackend.service;

import com.example.portfoliobackend.entity.Holding;
import com.example.portfoliobackend.entity.Portfolio;
import com.example.portfoliobackend.entity.PortfolioSnapshot;
import com.example.portfoliobackend.entity.PortfolioTarget;
import com.example.portfoliobackend.repository.HoldingRepository;
import com.example.portfoliobackend.repository.PortfolioRepository;
import com.example.portfoliobackend.repository.PortfolioSnapshotRepository;
import com.example.portfoliobackend.repository.PortfolioTargetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private static final String DEFAULT_CURRENCY = "INR";

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private PortfolioTargetRepository portfolioTargetRepository;

    @Autowired
    private PortfolioSnapshotRepository portfolioSnapshotRepository;

    public List<Portfolio> getAllPortfolios() {
        return portfolioRepository.findAll();
    }

    public List<Portfolio> getPortfoliosByUserId(Long userId) {
        return portfolioRepository.findAll().stream()
                .filter(portfolio -> Objects.equals(portfolio.getUserId(), userId))
                .collect(Collectors.toList());
    }

    public Portfolio getPortfolioById(Long portfolioId) {
        return portfolioRepository.findById(portfolioId).orElse(null);
    }

    @Transactional
    public Portfolio createPortfolio(Portfolio portfolio) {
        if (portfolio.getBaseCurrency() == null) {
            portfolio.setBaseCurrency(DEFAULT_CURRENCY);
        }
        return portfolioRepository.save(portfolio);
    }

    @Transactional
    public Portfolio updatePortfolio(Long portfolioId, Portfolio updated) {
        Optional<Portfolio> existing = portfolioRepository.findById(portfolioId);
        if (existing.isEmpty()) {
            return null;
        }

        Portfolio portfolio = existing.get();
        if (updated.getPortfolioName() != null) {
            portfolio.setPortfolioName(updated.getPortfolioName());
        }
        if (updated.getBaseCurrency() != null) {
            portfolio.setBaseCurrency(updated.getBaseCurrency());
        }
        if (updated.getUserId() != null) {
            portfolio.setUserId(updated.getUserId());
        }
        return portfolioRepository.save(portfolio);
    }

    @Transactional
    public boolean deletePortfolio(Long portfolioId) {
        if (!portfolioRepository.existsById(portfolioId)) {
            return false;
        }
        portfolioRepository.deleteById(portfolioId);
        return true;
    }

    public List<Holding> getHoldingsByPortfolioId(Long portfolioId) {
        return holdingRepository.findAll().stream()
                .filter(holding -> Objects.equals(holding.getPortfolioId(), portfolioId))
                .collect(Collectors.toList());
    }

    public Holding getHoldingById(Long holdingId) {
        return holdingRepository.findById(holdingId).orElse(null);
    }

    @Transactional
    public Holding addHolding(Holding holding) {
        if (holding.getCurrency() == null) {
            holding.setCurrency(DEFAULT_CURRENCY);
        }
        return holdingRepository.save(holding);
    }

    @Transactional
    public Holding updateHolding(Long holdingId, Holding updated) {
        Optional<Holding> existing = holdingRepository.findById(holdingId);
        if (existing.isEmpty()) {
            return null;
        }

        Holding holding = existing.get();
        if (updated.getAssetName() != null) {
            holding.setAssetName(updated.getAssetName());
        }
        if (updated.getAssetType() != null) {
            holding.setAssetType(updated.getAssetType());
        }
        if (updated.getQuantity() != null) {
            holding.setQuantity(updated.getQuantity());
        }
        if (updated.getPurchasePrice() != null) {
            holding.setPurchasePrice(updated.getPurchasePrice());
        }
        if (updated.getCurrentPrice() != null) {
            holding.setCurrentPrice(updated.getCurrentPrice());
        }
        if (updated.getCurrency() != null) {
            holding.setCurrency(updated.getCurrency());
        }
        if (updated.getPurchaseDate() != null) {
            holding.setPurchaseDate(updated.getPurchaseDate());
        }
        if (updated.getPortfolioId() != null) {
            holding.setPortfolioId(updated.getPortfolioId());
        }
        return holdingRepository.save(holding);
    }

    @Transactional
    public boolean deleteHolding(Long holdingId) {
        if (!holdingRepository.existsById(holdingId)) {
            return false;
        }
        holdingRepository.deleteById(holdingId);
        return true;
    }

    public List<PortfolioTarget> getTargetsByPortfolioId(Long portfolioId) {
        return portfolioTargetRepository.findAll().stream()
                .filter(target -> Objects.equals(target.getPortfolioId(), portfolioId))
                .collect(Collectors.toList());
    }

    public PortfolioTarget getTargetById(Long targetId) {
        return portfolioTargetRepository.findById(targetId).orElse(null);
    }

    @Transactional
    public PortfolioTarget addTarget(PortfolioTarget target) {
        return portfolioTargetRepository.save(target);
    }

    @Transactional
    public PortfolioTarget updateTarget(Long targetId, PortfolioTarget updated) {
        Optional<PortfolioTarget> existing = portfolioTargetRepository.findById(targetId);
        if (existing.isEmpty()) {
            return null;
        }

        PortfolioTarget target = existing.get();
        if (updated.getAssetType() != null) {
            target.setAssetType(updated.getAssetType());
        }
        if (updated.getTargetPercentage() != null) {
            target.setTargetPercentage(updated.getTargetPercentage());
        }
        if (updated.getPortfolioId() != null) {
            target.setPortfolioId(updated.getPortfolioId());
        }
        return portfolioTargetRepository.save(target);
    }

    @Transactional
    public boolean deleteTarget(Long targetId) {
        if (!portfolioTargetRepository.existsById(targetId)) {
            return false;
        }
        portfolioTargetRepository.deleteById(targetId);
        return true;
    }

    public List<PortfolioSnapshot> getSnapshotsByPortfolioId(Long portfolioId) {
        return portfolioSnapshotRepository.findAll().stream()
                .filter(snapshot -> Objects.equals(snapshot.getPortfolioId(), portfolioId))
                .collect(Collectors.toList());
    }

    public PortfolioSnapshot getSnapshotById(Long snapshotId) {
        return portfolioSnapshotRepository.findById(snapshotId).orElse(null);
    }

    @Transactional
    public PortfolioSnapshot updateSnapshot(Long snapshotId, PortfolioSnapshot updated) {
        Optional<PortfolioSnapshot> existing = portfolioSnapshotRepository.findById(snapshotId);
        if (existing.isEmpty()) {
            return null;
        }

        PortfolioSnapshot snapshot = existing.get();
        if (updated.getTotalValue() != null) {
            snapshot.setTotalValue(updated.getTotalValue());
        }
        if (updated.getCurrency() != null) {
            snapshot.setCurrency(updated.getCurrency());
        }
        if (updated.getSnapshotDate() != null) {
            snapshot.setSnapshotDate(updated.getSnapshotDate());
        }
        if (updated.getPortfolioId() != null) {
            snapshot.setPortfolioId(updated.getPortfolioId());
        }
        return portfolioSnapshotRepository.save(snapshot);
    }

    @Transactional
    public boolean deleteSnapshot(Long snapshotId) {
        if (!portfolioSnapshotRepository.existsById(snapshotId)) {
            return false;
        }
        portfolioSnapshotRepository.deleteById(snapshotId);
        return true;
    }

    @Transactional
    public PortfolioSnapshot recordSnapshot(Long portfolioId, BigDecimal totalValue, String currency) {
        PortfolioSnapshot snapshot = new PortfolioSnapshot();
        snapshot.setPortfolioId(portfolioId);
        snapshot.setTotalValue(totalValue);
        snapshot.setCurrency(currency == null ? DEFAULT_CURRENCY : currency);
        snapshot.setSnapshotDate(LocalDate.now());
        return portfolioSnapshotRepository.save(snapshot);
    }

    public BigDecimal calculateTotalValue(Long portfolioId) {
        BigDecimal total = BigDecimal.ZERO;
        for (Holding holding : getHoldingsByPortfolioId(portfolioId)) {
            if (holding.getQuantity() == null || holding.getCurrentPrice() == null) {
                continue;
            }
            total = total.add(holding.getQuantity().multiply(holding.getCurrentPrice()));
        }
        return total;
    }

    public List<String> listAssetTypes(Long portfolioId) {
        return getHoldingsByPortfolioId(portfolioId).stream()
                .map(Holding::getAssetType)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<PortfolioSnapshot> refreshAndGetSnapshots(Long portfolioId, String currency) {
        BigDecimal totalValue = calculateTotalValue(portfolioId);
        recordSnapshot(portfolioId, totalValue, currency);
        List<PortfolioSnapshot> snapshots = getSnapshotsByPortfolioId(portfolioId);
        return snapshots == null ? new ArrayList<>() : snapshots;
    }
}
