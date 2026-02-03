package com.example.portfoliobackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolios")
@Data
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_id")
    private Long portfolioId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "portfolio_name", nullable = false)
    private String portfolioName;

    @Column(name = "base_currency", length = 3)
    private String baseCurrency = "INR";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
