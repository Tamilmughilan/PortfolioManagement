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

    @Column(name = "asset_name", nullable = false)
    private String assetName;

    @Column(name = "asset_type", nullable = false)
    private String assetType;

    private BigDecimal quantity;

    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;

    @Column(name = "current_price")
    private BigDecimal currentPrice;

    @Column(length = 3)
    private String currency = "INR";

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;
}
