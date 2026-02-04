package com.example.portfoliobackend.repository;

import com.example.portfoliobackend.entity.PortfolioGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioGoalRepository extends JpaRepository<PortfolioGoal, Long> {
    List<PortfolioGoal> findByPortfolioId(Long portfolioId);
}
