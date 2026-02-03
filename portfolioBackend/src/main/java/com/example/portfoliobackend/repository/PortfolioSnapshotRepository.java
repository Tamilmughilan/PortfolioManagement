package com.example.portfoliobackend.repository;

import com.example.portfoliobackend.entity.PortfolioSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioSnapshotRepository extends JpaRepository<PortfolioSnapshot, Long> {
    List<PortfolioSnapshot> findByPortfolioIdOrderBySnapshotDateDesc(Long portfolioId);
}
