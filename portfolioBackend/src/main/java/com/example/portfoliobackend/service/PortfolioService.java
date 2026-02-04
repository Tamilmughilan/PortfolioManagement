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
import java.math.RoundingMode;
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
        return portfolioRepository.findByUserId(userId);
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
        if (!existing.isPresent()) {
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
        return holdingRepository.findByPortfolioId(portfolioId);
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
        if (!existing.isPresent()) {
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
        return portfolioTargetRepository.findByPortfolioId(portfolioId);
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
        if (!existing.isPresent()) {
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
        return portfolioSnapshotRepository.findByPortfolioIdOrderBySnapshotDateDesc(portfolioId);
    }

    public List<PortfolioSnapshot> getSnapshotsByPortfolioIdAsc(Long portfolioId) {
        return portfolioSnapshotRepository.findByPortfolioIdOrderBySnapshotDateAsc(portfolioId);
    }

    public PortfolioSnapshot getSnapshotById(Long snapshotId) {
        return portfolioSnapshotRepository.findById(snapshotId).orElse(null);
    }

    @Transactional
    public PortfolioSnapshot updateSnapshot(Long snapshotId, PortfolioSnapshot updated) {
        Optional<PortfolioSnapshot> existing = portfolioSnapshotRepository.findById(snapshotId);
        if (!existing.isPresent()) {
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

    @Transactional
    public List<PortfolioSnapshot> refreshAndGetSnapshots(Long portfolioId, String currency) {
        BigDecimal totalValue = calculateTotalValue(portfolioId);
        recordSnapshot(portfolioId, totalValue, currency);
        List<PortfolioSnapshot> snapshots = getSnapshotsByPortfolioId(portfolioId);
        return snapshots == null ? new ArrayList<>() : snapshots;
    }

    // New method for dashboard
    public com.example.portfoliobackend.dto.PortfolioDashboardDTO getPortfolioDashboard(Long portfolioId) {
        Portfolio portfolio = getPortfolioById(portfolioId);
        if (portfolio == null) {
            return null;
        }

        List<Holding> holdings = getHoldingsByPortfolioId(portfolioId);
        BigDecimal totalValue = calculateTotalValue(portfolioId);

        List<com.example.portfoliobackend.dto.PortfolioDashboardDTO.HoldingDetailDTO> holdingDTOs = holdings.stream()
                .map(holding -> {
                    com.example.portfoliobackend.dto.PortfolioDashboardDTO.HoldingDetailDTO dto =
                        new com.example.portfoliobackend.dto.PortfolioDashboardDTO.HoldingDetailDTO(
                            holding.getHoldingId(),
                            holding.getAssetName(),
                            holding.getAssetType(),
                            holding.getQuantity(),
                            holding.getPurchasePrice(),
                            holding.getCurrentPrice(),
                            holding.getCurrency(),
                            holding.getPurchaseDate()
                        );

                    // Calculate allocation percentage
                    if (totalValue.compareTo(BigDecimal.ZERO) > 0 && holding.getQuantity() != null && holding.getCurrentPrice() != null) {
                        BigDecimal holdingValue = holding.getQuantity().multiply(holding.getCurrentPrice());
                        BigDecimal allocation = holdingValue.divide(totalValue, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal(100));
                        dto.setAllocation(allocation);
                    } else {
                        dto.setAllocation(BigDecimal.ZERO);
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        return new com.example.portfoliobackend.dto.PortfolioDashboardDTO(
                portfolio.getPortfolioId(),
                portfolio.getPortfolioName(),
                portfolio.getBaseCurrency(),
                totalValue,
                holdingDTOs,
                portfolio.getCreatedAt()
        );
    }

    public com.example.portfoliobackend.dto.PortfolioDriftDTO getPortfolioDriftStory(Long portfolioId) {
        Portfolio portfolio = getPortfolioById(portfolioId);
        if (portfolio == null) {
            return null;
        }

        List<PortfolioSnapshot> snapshots = getSnapshotsByPortfolioIdAsc(portfolioId);
        BigDecimal initialValue;
        BigDecimal latestValue;
        LocalDate initialDate;
        LocalDate latestDate;

        if (snapshots == null || snapshots.isEmpty()) {
            initialValue = calculateTotalValue(portfolioId);
            latestValue = initialValue;
            LocalDate fallbackDate = portfolio.getCreatedAt() != null
                    ? portfolio.getCreatedAt().toLocalDate()
                    : LocalDate.now();
            initialDate = fallbackDate;
            latestDate = LocalDate.now();
            snapshots = new ArrayList<>();
            PortfolioSnapshot generated = new PortfolioSnapshot();
            generated.setPortfolioId(portfolioId);
            generated.setTotalValue(initialValue);
            generated.setCurrency(portfolio.getBaseCurrency());
            generated.setSnapshotDate(fallbackDate);
            snapshots.add(generated);
        } else {
            PortfolioSnapshot first = snapshots.get(0);
            PortfolioSnapshot last = snapshots.get(snapshots.size() - 1);
            initialValue = first.getTotalValue() == null ? BigDecimal.ZERO : first.getTotalValue();
            latestValue = last.getTotalValue() == null ? BigDecimal.ZERO : last.getTotalValue();
            initialDate = first.getSnapshotDate();
            latestDate = last.getSnapshotDate();
        }

        BigDecimal driftValue = latestValue.subtract(initialValue);
        BigDecimal driftPercent = BigDecimal.ZERO;
        if (initialValue.compareTo(BigDecimal.ZERO) != 0) {
            driftPercent = driftValue.divide(initialValue, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
        }

        List<com.example.portfoliobackend.dto.PortfolioDriftDTO.TimelineEntry> timeline = snapshots.stream()
                .map(snapshot -> {
                    BigDecimal totalValue = snapshot.getTotalValue() == null ? BigDecimal.ZERO : snapshot.getTotalValue();
                    BigDecimal delta = totalValue.subtract(initialValue);
                    BigDecimal percent = BigDecimal.ZERO;
                    if (initialValue.compareTo(BigDecimal.ZERO) != 0) {
                        percent = delta.divide(initialValue, 4, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal(100));
                    }
                    String story = percent.compareTo(BigDecimal.ZERO) >= 0
                            ? "The portfolio climbed above its starting point."
                            : "The portfolio dipped below its starting point.";
                    return new com.example.portfoliobackend.dto.PortfolioDriftDTO.TimelineEntry(
                            snapshot.getSnapshotDate(),
                            totalValue,
                            delta,
                            percent,
                            story
                    );
                })
                .collect(Collectors.toList());

        String narrative = "Since " + initialDate + ", your portfolio moved from " + initialValue
                + " to " + latestValue + ", a drift of " + driftPercent.setScale(2, RoundingMode.HALF_UP) + "%.";

        return new com.example.portfoliobackend.dto.PortfolioDriftDTO(
                portfolio.getPortfolioId(),
                portfolio.getPortfolioName(),
                portfolio.getBaseCurrency(),
                initialValue,
                latestValue,
                driftValue,
                driftPercent,
                initialDate,
                latestDate,
                narrative,
                timeline
        );
    }
}