package com.example.portfoliobackend.controller;

import com.example.portfoliobackend.entity.Holding;
import com.example.portfoliobackend.entity.Portfolio;
import com.example.portfoliobackend.entity.PortfolioSnapshot;
import com.example.portfoliobackend.entity.PortfolioTarget;
import com.example.portfoliobackend.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@CrossOrigin
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @GetMapping
    public ResponseEntity<List<Portfolio>> getAllPortfolios() {
        return ResponseEntity.ok(portfolioService.getAllPortfolios());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Portfolio>> getPortfoliosByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(portfolioService.getPortfoliosByUserId(userId));
    }

    @GetMapping("/{portfolioId}")
    public ResponseEntity<Portfolio> getPortfolioById(@PathVariable Long portfolioId) {
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
        if (portfolio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(portfolio);
    }

    @PostMapping
    public ResponseEntity<Portfolio> createPortfolio(@RequestBody Portfolio portfolio) {
        Portfolio created = portfolioService.createPortfolio(portfolio);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{portfolioId}")
    public ResponseEntity<Portfolio> updatePortfolio(
            @PathVariable Long portfolioId,
            @RequestBody Portfolio portfolio
    ) {
        Portfolio updated = portfolioService.updatePortfolio(portfolioId, portfolio);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long portfolioId) {
        if (!portfolioService.deletePortfolio(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{portfolioId}/holdings")
    public ResponseEntity<List<Holding>> getHoldings(@PathVariable Long portfolioId) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(portfolioService.getHoldingsByPortfolioId(portfolioId));
    }

    @GetMapping("/{portfolioId}/holdings/{holdingId}")
    public ResponseEntity<Holding> getHolding(
            @PathVariable Long portfolioId,
            @PathVariable Long holdingId
    ) {
        Holding holding = portfolioService.getHoldingById(holdingId);
        if (holding == null || holding.getPortfolioId() == null || !holding.getPortfolioId().equals(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(holding);
    }

    @PostMapping("/{portfolioId}/holdings")
    public ResponseEntity<Holding> addHolding(
            @PathVariable Long portfolioId,
            @RequestBody Holding holding
    ) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        holding.setPortfolioId(portfolioId);
        Holding created = portfolioService.addHolding(holding);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{portfolioId}/holdings/{holdingId}")
    public ResponseEntity<Holding> updateHolding(
            @PathVariable Long portfolioId,
            @PathVariable Long holdingId,
            @RequestBody Holding holding
    ) {
        Holding existing = portfolioService.getHoldingById(holdingId);
        if (existing == null || existing.getPortfolioId() == null || !existing.getPortfolioId().equals(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        holding.setPortfolioId(portfolioId);
        Holding updated = portfolioService.updateHolding(holdingId, holding);
        return ResponseEntity.ok(updated);
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
    public ResponseEntity<List<PortfolioTarget>> getTargets(@PathVariable Long portfolioId) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(portfolioService.getTargetsByPortfolioId(portfolioId));
    }

    @GetMapping("/{portfolioId}/targets/{targetId}")
    public ResponseEntity<PortfolioTarget> getTarget(
            @PathVariable Long portfolioId,
            @PathVariable Long targetId
    ) {
        PortfolioTarget target = portfolioService.getTargetById(targetId);
        if (target == null || target.getPortfolioId() == null || !target.getPortfolioId().equals(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(target);
    }

    @PostMapping("/{portfolioId}/targets")
    public ResponseEntity<PortfolioTarget> addTarget(
            @PathVariable Long portfolioId,
            @RequestBody PortfolioTarget target
    ) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        target.setPortfolioId(portfolioId);
        PortfolioTarget created = portfolioService.addTarget(target);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{portfolioId}/targets/{targetId}")
    public ResponseEntity<PortfolioTarget> updateTarget(
            @PathVariable Long portfolioId,
            @PathVariable Long targetId,
            @RequestBody PortfolioTarget target
    ) {
        PortfolioTarget existing = portfolioService.getTargetById(targetId);
        if (existing == null || existing.getPortfolioId() == null || !existing.getPortfolioId().equals(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        target.setPortfolioId(portfolioId);
        PortfolioTarget updated = portfolioService.updateTarget(targetId, target);
        return ResponseEntity.ok(updated);
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
    public ResponseEntity<List<PortfolioSnapshot>> getSnapshots(@PathVariable Long portfolioId) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(portfolioService.getSnapshotsByPortfolioId(portfolioId));
    }

    @GetMapping("/{portfolioId}/snapshots/{snapshotId}")
    public ResponseEntity<PortfolioSnapshot> getSnapshot(
            @PathVariable Long portfolioId,
            @PathVariable Long snapshotId
    ) {
        PortfolioSnapshot snapshot = portfolioService.getSnapshotById(snapshotId);
        if (snapshot == null || snapshot.getPortfolioId() == null || !snapshot.getPortfolioId().equals(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(snapshot);
    }

    @PostMapping("/{portfolioId}/snapshots")
    public ResponseEntity<PortfolioSnapshot> recordSnapshot(
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
        return ResponseEntity.status(HttpStatus.CREATED).body(snapshot);
    }

    @PostMapping("/{portfolioId}/snapshots/refresh")
    public ResponseEntity<List<PortfolioSnapshot>> refreshSnapshots(
            @PathVariable Long portfolioId,
            @RequestParam(value = "currency", required = false) String currency
    ) {
        if (portfolioService.getPortfolioById(portfolioId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(portfolioService.refreshAndGetSnapshots(portfolioId, currency));
    }

    @PutMapping("/{portfolioId}/snapshots/{snapshotId}")
    public ResponseEntity<PortfolioSnapshot> updateSnapshot(
            @PathVariable Long portfolioId,
            @PathVariable Long snapshotId,
            @RequestBody PortfolioSnapshot snapshot
    ) {
        PortfolioSnapshot existing = portfolioService.getSnapshotById(snapshotId);
        if (existing == null || existing.getPortfolioId() == null || !existing.getPortfolioId().equals(portfolioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        snapshot.setPortfolioId(portfolioId);
        PortfolioSnapshot updated = portfolioService.updateSnapshot(snapshotId, snapshot);
        return ResponseEntity.ok(updated);
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
}