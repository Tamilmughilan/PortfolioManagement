package com.example.portfoliobackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "portfolio_snapshots")
@Data
public class PortfolioSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "snapshot_id")
    private Long snapshotId;

    @Column(name = "portfolio_id", nullable = false)
    private Long portfolioId;

    @Column(name = "total_value")
    private BigDecimal totalValue;

    @Column(length = 3)
    private String currency = "INR";

    @Column(name = "snapshot_date")
    private LocalDate snapshotDate;
}
