package com.example.portfoliobackend.repository;

import com.example.portfoliobackend.entity.PortfolioTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioTargetRepository extends JpaRepository<PortfolioTarget, Long> {
    List<PortfolioTarget> findByPortfolioId(Long portfolioId);
}
