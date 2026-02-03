package com.example.portfoliobackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "portfolio_targets")
@Data
public class PortfolioTarget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "portfolio_id", nullable = false)
    private Long portfolioId;

    @Column(name = "asset_type", nullable = false)
    private String assetType;

    @Column(name = "target_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal targetPercentage;
}
