package com.example.portfoliobackend.repository;

import com.example.portfoliobackend.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByUserId(Long userId);
    List<Portfolio> findByPortfolioNameContainingIgnoreCase(String name);
}
