package com.example.portfoliobackend.controller;

import com.example.portfoliobackend.dto.GoalForecastDTO;
import com.example.portfoliobackend.dto.HoldingDTO;
import com.example.portfoliobackend.dto.PortfolioDTO;
import com.example.portfoliobackend.dto.PortfolioGoalDTO;
import com.example.portfoliobackend.dto.PortfolioSnapshotDTO;
import com.example.portfoliobackend.dto.PortfolioTargetDTO;
import com.example.portfoliobackend.dto.TrendForecastDTO;
import com.example.portfoliobackend.dto.WhatIfRequestDTO;
import com.example.portfoliobackend.dto.WhatIfResponseDTO;
import com.example.portfoliobackend.entity.Holding;
import com.example.portfoliobackend.entity.Portfolio;
import com.example.portfoliobackend.entity.PortfolioGoal;
import com.example.portfoliobackend.entity.PortfolioSnapshot;
import com.example.portfoliobackend.entity.PortfolioTarget;
import com.example.portfoliobackend.service.ForecastService;
import com.example.portfoliobackend.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/portfolios")
@CrossOrigin
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private ForecastService forecastService;

    @GetMapping
    public ResponseEntity<List<PortfolioDTO>> getAllPortfolios() {
        List<PortfolioDTO> results = portfolioService.getAllPortfolios().stream()
                .map(PortfolioController::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PortfolioDTO>> getPortfoliosByUser(@PathVariable Long userId) {
        List<PortfolioDTO> results = portfolioService.getPortfoliosByUserId(userId).stream()
                .map(PortfolioController::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{portfolioId}")
    public ResponseEntity<PortfolioDTO> getPortfolioById(@PathVariable Long portfolioId) {
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
        if (portfolio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(toDTO(portfolio));
    }

    @PostMapping
    public ResponseEntity<PortfolioDTO> createPortfolio(@RequestBody PortfolioDTO portfolio) {
        Portfolio toCreate = new Portfolio();
        toCreate.setUserId(portfolio.getUserId());
        toCreate.setPortfolioName(portfolio.getPortfolioName());
        toCreate.setBaseCurrency(portfolio.getBaseCurrency());
        Portfolio created = portfolioService.createPortfolio(toCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(created));
    }

    @PutMapping("/{portfolioId}")
    public ResponseEntity<PortfolioDTO> updatePortfolio(
            @PathVariable Long portfolioId,
            @RequestBody PortfolioDTO portfolio
    ) {
        Portfolio toUpdate = new Portfolio();
        toUpdate.setUserId(portfolio.getUserId());
        toUpdate.setPortfolioName(portfolio.getPortfolioName());
        toUpdate.setBaseCurrency(portfolio.getBaseCurrency());
        Portfolio updated = portfolioService.updatePortfolio(portfolioId, toUpdate);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(toDTO(updated));
    }

    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long portfolioId) {
        if (!portfolioService.deletePortfolio(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{portfolioId}/holdings")
    public ResponseEntity<List<HoldingDTO>> getHoldings(@PathVariable Long portfolioId) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<HoldingDTO> results = portfolioService.getHoldingsByPortfolioId(portfolioId).stream()
                .map(PortfolioController::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{portfolioId}/holdings/{holdingId}")
    public ResponseEntity<HoldingDTO> getHolding(
            @PathVariable Long portfolioId,
            @PathVariable Long holdingId
    ) {
        Holding holding = portfolioService.getHoldingById(holdingId);
        if (holding == null || holding.getPortfolioId() == null || !holding.getPortfolioId().equals(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(toDTO(holding));
    }

    @PostMapping("/{portfolioId}/holdings")
    public ResponseEntity<HoldingDTO> addHolding(
            @PathVariable Long portfolioId,
            @RequestBody HoldingDTO holding
    ) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Holding toCreate = toEntity(holding);
        toCreate.setPortfolioId(portfolioId);
        Holding created = portfolioService.addHolding(toCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(created));
    }

    @PutMapping("/{portfolioId}/holdings/{holdingId}")
    public ResponseEntity<HoldingDTO> updateHolding(
            @PathVariable Long portfolioId,
            @PathVariable Long holdingId,
            @RequestBody HoldingDTO holding
    ) {
        Holding existing = portfolioService.getHoldingById(holdingId);
        if (existing == null || existing.getPortfolioId() == null || !existing.getPortfolioId().equals(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Holding toUpdate = toEntity(holding);
        toUpdate.setPortfolioId(portfolioId);
        Holding updated = portfolioService.updateHolding(holdingId, toUpdate);
        return ResponseEntity.ok(toDTO(updated));
    }

    @DeleteMapping("/{portfolioId}/holdings/{holdingId}")
    public ResponseEntity<Void> deleteHolding(
            @PathVariable Long portfolioId,
            @PathVariable Long holdingId
    ) {
        Holding existing = portfolioService.getHoldingById(holdingId);
        if (existing == null || existing.getPortfolioId() == null || !existing.getPortfolioId().equals(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!portfolioService.deleteHolding(holdingId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{portfolioId}/targets")
    public ResponseEntity<List<PortfolioTargetDTO>> getTargets(@PathVariable Long portfolioId) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<PortfolioTargetDTO> results = portfolioService.getTargetsByPortfolioId(portfolioId).stream()
                .map(PortfolioController::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{portfolioId}/goals")
    public ResponseEntity<List<PortfolioGoalDTO>> getGoals(@PathVariable Long portfolioId) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<PortfolioGoalDTO> results = portfolioService.getGoalsByPortfolioId(portfolioId).stream()
                .map(PortfolioController::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{portfolioId}/goals/{goalId}")
    public ResponseEntity<PortfolioGoalDTO> getGoal(
            @PathVariable Long portfolioId,
            @PathVariable Long goalId
    ) {
        PortfolioGoal goal = portfolioService.getGoalById(goalId);
        if (goal == null || goal.getPortfolioId() == null || !goal.getPortfolioId().equals(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(toDTO(goal));
    }

    @PostMapping("/{portfolioId}/goals")
    public ResponseEntity<PortfolioGoalDTO> addGoal(
            @PathVariable Long portfolioId,
            @RequestBody PortfolioGoalDTO goal
    ) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        PortfolioGoal toCreate = toEntity(goal);
        toCreate.setPortfolioId(portfolioId);
        PortfolioGoal created = portfolioService.addGoal(toCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(created));
    }

    @PutMapping("/{portfolioId}/goals/{goalId}")
    public ResponseEntity<PortfolioGoalDTO> updateGoal(
            @PathVariable Long portfolioId,
            @PathVariable Long goalId,
            @RequestBody PortfolioGoalDTO goal
    ) {
        PortfolioGoal existing = portfolioService.getGoalById(goalId);
        if (existing == null || existing.getPortfolioId() == null || !existing.getPortfolioId().equals(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        PortfolioGoal toUpdate = toEntity(goal);
        toUpdate.setPortfolioId(portfolioId);
        PortfolioGoal updated = portfolioService.updateGoal(goalId, toUpdate);
        return ResponseEntity.ok(toDTO(updated));
    }

    @DeleteMapping("/{portfolioId}/goals/{goalId}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long portfolioId,
            @PathVariable Long goalId
    ) {
        PortfolioGoal existing = portfolioService.getGoalById(goalId);
        if (existing == null || existing.getPortfolioId() == null || !existing.getPortfolioId().equals(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!portfolioService.deleteGoal(goalId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{portfolioId}/targets/{targetId}")
    public ResponseEntity<PortfolioTargetDTO> getTarget(
            @PathVariable Long portfolioId,
            @PathVariable Long targetId
    ) {
        PortfolioTarget target = portfolioService.getTargetById(targetId);
        if (target == null || target.getPortfolioId() == null || !target.getPortfolioId().equals(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(toDTO(target));
    }

    @PostMapping("/{portfolioId}/targets")
    public ResponseEntity<PortfolioTargetDTO> addTarget(
            @PathVariable Long portfolioId,
            @RequestBody PortfolioTargetDTO target
    ) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        PortfolioTarget toCreate = toEntity(target);
        toCreate.setPortfolioId(portfolioId);
        PortfolioTarget created = portfolioService.addTarget(toCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(created));
    }

    @PutMapping("/{portfolioId}/targets/{targetId}")
    public ResponseEntity<PortfolioTargetDTO> updateTarget(
            @PathVariable Long portfolioId,
            @PathVariable Long targetId,
            @RequestBody PortfolioTargetDTO target
    ) {
        PortfolioTarget existing = portfolioService.getTargetById(targetId);
        if (existing == null || existing.getPortfolioId() == null || !existing.getPortfolioId().equals(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        PortfolioTarget toUpdate = toEntity(target);
        toUpdate.setPortfolioId(portfolioId);
        PortfolioTarget updated = portfolioService.updateTarget(targetId, toUpdate);
        return ResponseEntity.ok(toDTO(updated));
    }

    @DeleteMapping("/{portfolioId}/targets/{targetId}")
    public ResponseEntity<Void> deleteTarget(
            @PathVariable Long portfolioId,
            @PathVariable Long targetId
    ) {
        PortfolioTarget existing = portfolioService.getTargetById(targetId);
        if (existing == null || existing.getPortfolioId() == null || !existing.getPortfolioId().equals(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!portfolioService.deleteTarget(targetId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{portfolioId}/snapshots")
    public ResponseEntity<List<PortfolioSnapshotDTO>> getSnapshots(@PathVariable Long portfolioId) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<PortfolioSnapshotDTO> results = portfolioService.getSnapshotsByPortfolioId(portfolioId).stream()
                .map(PortfolioController::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{portfolioId}/snapshots/{snapshotId}")
    public ResponseEntity<PortfolioSnapshotDTO> getSnapshot(
            @PathVariable Long portfolioId,
            @PathVariable Long snapshotId
    ) {
        PortfolioSnapshot snapshot = portfolioService.getSnapshotById(snapshotId);
        if (snapshot == null || snapshot.getPortfolioId() == null || !snapshot.getPortfolioId().equals(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(toDTO(snapshot));
    }

    @PostMapping("/{portfolioId}/snapshots")
    public ResponseEntity<PortfolioSnapshotDTO> recordSnapshot(
            @PathVariable Long portfolioId,
            @RequestBody SnapshotRequest request
    ) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        BigDecimal totalValue = request != null ? request.getTotalValue() : null;
        if (totalValue == null) {
            totalValue = portfolioService.calculateTotalValue(portfolioId);
        }
        String currency = request != null ? request.getCurrency() : null;
        PortfolioSnapshot snapshot = portfolioService.recordSnapshot(portfolioId, totalValue, currency);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(snapshot));
    }

    @PostMapping("/{portfolioId}/snapshots/refresh")
    public ResponseEntity<List<PortfolioSnapshotDTO>> refreshSnapshots(
            @PathVariable Long portfolioId,
            @RequestParam(value = "currency", required = false) String currency
    ) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<PortfolioSnapshotDTO> results = portfolioService.refreshAndGetSnapshots(portfolioId, currency).stream()
                .map(PortfolioController::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @PutMapping("/{portfolioId}/snapshots/{snapshotId}")
    public ResponseEntity<PortfolioSnapshotDTO> updateSnapshot(
            @PathVariable Long portfolioId,
            @PathVariable Long snapshotId,
            @RequestBody PortfolioSnapshotDTO snapshot
    ) {
        PortfolioSnapshot existing = portfolioService.getSnapshotById(snapshotId);
        if (existing == null || existing.getPortfolioId() == null || !existing.getPortfolioId().equals(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        PortfolioSnapshot toUpdate = toEntity(snapshot);
        toUpdate.setPortfolioId(portfolioId);
        PortfolioSnapshot updated = portfolioService.updateSnapshot(snapshotId, toUpdate);
        return ResponseEntity.ok(toDTO(updated));
    }

    @DeleteMapping("/{portfolioId}/snapshots/{snapshotId}")
    public ResponseEntity<Void> deleteSnapshot(
            @PathVariable Long portfolioId,
            @PathVariable Long snapshotId
    ) {
        PortfolioSnapshot existing = portfolioService.getSnapshotById(snapshotId);
        if (existing == null || existing.getPortfolioId() == null || !existing.getPortfolioId().equals(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!portfolioService.deleteSnapshot(snapshotId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{portfolioId}/total-value")
    public ResponseEntity<BigDecimal> getTotalValue(@PathVariable Long portfolioId) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(portfolioService.calculateTotalValue(portfolioId));
    }

    @GetMapping("/{portfolioId}/asset-types")
    public ResponseEntity<List<String>> getAssetTypes(@PathVariable Long portfolioId) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(portfolioService.listAssetTypes(portfolioId));
    }

    @GetMapping("/{portfolioId}/dashboard")
    public ResponseEntity<com.example.portfoliobackend.dto.PortfolioDashboardDTO> getPortfolioDashboard(@PathVariable Long portfolioId) {
        com.example.portfoliobackend.dto.PortfolioDashboardDTO dashboard = portfolioService.getPortfolioDashboard(portfolioId);
        if (dashboard == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/{portfolioId}/drift-story")
    public ResponseEntity<com.example.portfoliobackend.dto.PortfolioDriftDTO> getPortfolioDriftStory(@PathVariable Long portfolioId) {
        com.example.portfoliobackend.dto.PortfolioDriftDTO drift = portfolioService.getPortfolioDriftStory(portfolioId);
        if (drift == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(drift);
    }

    @GetMapping("/{portfolioId}/goals/{goalId}/forecast")
    public ResponseEntity<GoalForecastDTO> forecastGoal(
            @PathVariable Long portfolioId,
            @PathVariable Long goalId
    ) {
        PortfolioGoal goal = portfolioService.getGoalById(goalId);
        if (goal == null || goal.getPortfolioId() == null || !goal.getPortfolioId().equals(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(forecastService.forecastGoal(portfolioId, goal));
    }

    @PostMapping("/{portfolioId}/what-if")
    public ResponseEntity<WhatIfResponseDTO> whatIf(
            @PathVariable Long portfolioId,
            @RequestBody WhatIfRequestDTO request
    ) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(forecastService.runWhatIf(portfolioId, request));
    }

    @GetMapping("/{portfolioId}/forecast-trend")
    public ResponseEntity<TrendForecastDTO> forecastTrend(
            @PathVariable Long portfolioId,
            @RequestParam(value = "months", required = false, defaultValue = "6") int months
    ) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        int monthsAhead = Math.max(1, Math.min(months, 24));
        return ResponseEntity.ok(forecastService.forecastTrend(portfolioId, monthsAhead));
    }

    public static class SnapshotRequest {
        private BigDecimal totalValue;
        private String currency;

        public BigDecimal getTotalValue() {
            return totalValue;
        }

        public void setTotalValue(BigDecimal totalValue) {
            this.totalValue = totalValue;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }

    private static PortfolioDTO toDTO(Portfolio portfolio) {
        return new PortfolioDTO(
                portfolio.getPortfolioId(),
                portfolio.getUserId(),
                portfolio.getPortfolioName(),
                portfolio.getBaseCurrency(),
                portfolio.getCreatedAt()
        );
    }

    private static HoldingDTO toDTO(Holding holding) {
        return new HoldingDTO(
                holding.getHoldingId(),
                holding.getPortfolioId(),
                holding.getAssetName(),
                holding.getAssetType(),
                holding.getQuantity(),
                holding.getPurchasePrice(),
                holding.getCurrentPrice(),
                holding.getCurrency(),
                holding.getPurchaseDate(),
                holding.getTargetValue()
        );
    }

    private static Holding toEntity(HoldingDTO dto) {
        Holding holding = new Holding();
        holding.setHoldingId(dto.getHoldingId());
        holding.setPortfolioId(dto.getPortfolioId());
        holding.setAssetName(dto.getAssetName());
        holding.setAssetType(dto.getAssetType());
        holding.setQuantity(dto.getQuantity());
        holding.setPurchasePrice(dto.getPurchasePrice());
        holding.setCurrentPrice(dto.getCurrentPrice());
        holding.setCurrency(dto.getCurrency());
        holding.setPurchaseDate(dto.getPurchaseDate());
        holding.setTargetValue(dto.getTargetValue());
        return holding;
    }

    private static PortfolioTargetDTO toDTO(PortfolioTarget target) {
        return new PortfolioTargetDTO(
                target.getTargetId(),
                target.getPortfolioId(),
                target.getAssetType(),
                target.getTargetPercentage()
        );
    }

    private static PortfolioTarget toEntity(PortfolioTargetDTO dto) {
        PortfolioTarget target = new PortfolioTarget();
        target.setTargetId(dto.getTargetId());
        target.setPortfolioId(dto.getPortfolioId());
        target.setAssetType(dto.getAssetType());
        target.setTargetPercentage(dto.getTargetPercentage());
        return target;
    }

    private static PortfolioSnapshotDTO toDTO(PortfolioSnapshot snapshot) {
        return new PortfolioSnapshotDTO(
                snapshot.getSnapshotId(),
                snapshot.getPortfolioId(),
                snapshot.getTotalValue(),
                snapshot.getCurrency(),
                snapshot.getSnapshotDate()
        );
    }

    private static PortfolioSnapshot toEntity(PortfolioSnapshotDTO dto) {
        PortfolioSnapshot snapshot = new PortfolioSnapshot();
        snapshot.setSnapshotId(dto.getSnapshotId());
        snapshot.setPortfolioId(dto.getPortfolioId());
        snapshot.setTotalValue(dto.getTotalValue());
        snapshot.setCurrency(dto.getCurrency());
        snapshot.setSnapshotDate(dto.getSnapshotDate());
        return snapshot;
    }

    private static PortfolioGoalDTO toDTO(PortfolioGoal goal) {
        return new PortfolioGoalDTO(
                goal.getGoalId(),
                goal.getPortfolioId(),
                goal.getGoalName(),
                goal.getTargetAmount(),
                goal.getTargetDate(),
                goal.getExpectedAnnualReturn(),
                goal.getCreatedAt()
        );
    }

    private static PortfolioGoal toEntity(PortfolioGoalDTO dto) {
        PortfolioGoal goal = new PortfolioGoal();
        goal.setGoalId(dto.getGoalId());
        goal.setPortfolioId(dto.getPortfolioId());
        goal.setGoalName(dto.getGoalName());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setTargetDate(dto.getTargetDate());
        goal.setExpectedAnnualReturn(dto.getExpectedAnnualReturn());
        return goal;
    }
}