package com.example.portfoliobackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "holdings")
@Data
public class Holding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "holding_id")
    private Long holdingId;

    @Column(name = "portfolio_id", nullable = false)
    private Long portfolioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", insertable = false, updatable = false)
    private Portfolio portfolio;

    @Column(name = "asset_name", nullable = false, length = 100)
    private String assetName;

    @Column(name = "asset_type", nullable = false, length = 50)
    private String assetType;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal quantity;

    @Column(name = "purchase_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "current_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentPrice;

    @Column(length = 3)
    private String currency = "INR";

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;
}
