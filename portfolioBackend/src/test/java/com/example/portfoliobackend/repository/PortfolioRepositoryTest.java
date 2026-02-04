package com.example.portfoliobackend.repository;

import com.example.portfoliobackend.entity.Portfolio;
import com.example.portfoliobackend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("PortfolioRepository Integration Tests")
class PortfolioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PortfolioRepository portfolioRepository;

    private User testUser;
    private Portfolio testPortfolio;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setDefaultCurrency("USD");
        testUser = entityManager.persistFlushFind(testUser);

        testPortfolio = new Portfolio();
        testPortfolio.setUserId(testUser.getUserId());
        testPortfolio.setPortfolioName("Test Portfolio");
        testPortfolio.setBaseCurrency("USD");
    }

    @Test
    @DisplayName("Should save portfolio successfully")
    void save_ShouldPersistPortfolio() {
        Portfolio saved = portfolioRepository.save(testPortfolio);

        assertThat(saved.getPortfolioId()).isNotNull();
        assertThat(saved.getPortfolioName()).isEqualTo("Test Portfolio");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find portfolio by ID")
    void findById_WhenExists_ShouldReturnPortfolio() {
        Portfolio persisted = entityManager.persistFlushFind(testPortfolio);

        Optional<Portfolio> found = portfolioRepository.findById(persisted.getPortfolioId());

        assertThat(found).isPresent();
        assertThat(found.get().getPortfolioName()).isEqualTo("Test Portfolio");
    }

    @Test
    @DisplayName("Should find portfolios by user ID")
    void findByUserId_ShouldReturnUserPortfolios() {
        Portfolio portfolio1 = new Portfolio();
        portfolio1.setUserId(testUser.getUserId());
        portfolio1.setPortfolioName("Portfolio 1");

        Portfolio portfolio2 = new Portfolio();
        portfolio2.setUserId(testUser.getUserId());
        portfolio2.setPortfolioName("Portfolio 2");

        entityManager.persist(portfolio1);
        entityManager.persist(portfolio2);
        entityManager.flush();

        List<Portfolio> portfolios = portfolioRepository.findByUserId(testUser.getUserId());

        assertThat(portfolios).hasSize(2);
        assertThat(portfolios).extracting(Portfolio::getPortfolioName)
                .containsExactlyInAnyOrder("Portfolio 1", "Portfolio 2");
    }

    @Test
    @DisplayName("Should return empty list when user has no portfolios")
    void findByUserId_WhenNoPortfolios_ShouldReturnEmptyList() {
        List<Portfolio> portfolios = portfolioRepository.findByUserId(999L);

        assertThat(portfolios).isEmpty();
    }

    @Test
    @DisplayName("Should find portfolios by name containing (case insensitive)")
    void findByPortfolioNameContainingIgnoreCase_ShouldReturnMatchingPortfolios() {
        Portfolio portfolio1 = new Portfolio();
        portfolio1.setUserId(testUser.getUserId());
        portfolio1.setPortfolioName("Retirement Fund");

        Portfolio portfolio2 = new Portfolio();
        portfolio2.setUserId(testUser.getUserId());
        portfolio2.setPortfolioName("Growth Portfolio");

        Portfolio portfolio3 = new Portfolio();
        portfolio3.setUserId(testUser.getUserId());
        portfolio3.setPortfolioName("My Retirement Plan");

        entityManager.persist(portfolio1);
        entityManager.persist(portfolio2);
        entityManager.persist(portfolio3);
        entityManager.flush();

        List<Portfolio> portfolios = portfolioRepository.findByPortfolioNameContainingIgnoreCase("retirement");

        assertThat(portfolios).hasSize(2);
        assertThat(portfolios).extracting(Portfolio::getPortfolioName)
                .containsExactlyInAnyOrder("Retirement Fund", "My Retirement Plan");
    }

    @Test
    @DisplayName("Should update portfolio successfully")
    void save_ShouldUpdateExistingPortfolio() {
        Portfolio persisted = entityManager.persistFlushFind(testPortfolio);

        persisted.setPortfolioName("Updated Portfolio");
        persisted.setBaseCurrency("EUR");
        portfolioRepository.save(persisted);
        entityManager.flush();
        entityManager.clear();

        Portfolio updated = entityManager.find(Portfolio.class, persisted.getPortfolioId());

        assertThat(updated.getPortfolioName()).isEqualTo("Updated Portfolio");
        assertThat(updated.getBaseCurrency()).isEqualTo("EUR");
    }

    @Test
    @DisplayName("Should delete portfolio successfully")
    void delete_ShouldRemovePortfolio() {
        Portfolio persisted = entityManager.persistFlushFind(testPortfolio);
        Long portfolioId = persisted.getPortfolioId();

        portfolioRepository.deleteById(portfolioId);
        entityManager.flush();

        Portfolio deleted = entityManager.find(Portfolio.class, portfolioId);
        assertThat(deleted).isNull();
    }

    @Test
    @DisplayName("Should set default currency when not provided")
    void save_WithoutCurrency_ShouldUseDefault() {
        Portfolio portfolioWithoutCurrency = new Portfolio();
        portfolioWithoutCurrency.setUserId(testUser.getUserId());
        portfolioWithoutCurrency.setPortfolioName("No Currency Portfolio");

        Portfolio saved = portfolioRepository.save(portfolioWithoutCurrency);

        assertThat(saved.getBaseCurrency()).isEqualTo("INR");
    }
}