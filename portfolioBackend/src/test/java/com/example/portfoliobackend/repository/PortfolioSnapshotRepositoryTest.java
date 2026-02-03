package com.example.portfoliobackend.repository;

import com.example.portfoliobackend.entity.Portfolio;
import com.example.portfoliobackend.entity.PortfolioSnapshot;
import com.example.portfoliobackend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("PortfolioSnapshotRepository Integration Tests")
class PortfolioSnapshotRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PortfolioSnapshotRepository portfolioSnapshotRepository;

    private User testUser;
    private Portfolio testPortfolio;
    private PortfolioSnapshot testSnapshot;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser = entityManager.persistFlushFind(testUser);

        testPortfolio = new Portfolio();
        testPortfolio.setUserId(testUser.getUserId());
        testPortfolio.setPortfolioName("Test Portfolio");
        testPortfolio = entityManager.persistFlushFind(testPortfolio);

        testSnapshot = new PortfolioSnapshot();
        testSnapshot.setPortfolioId(testPortfolio.getPortfolioId());
        testSnapshot.setTotalValue(new BigDecimal("10000.00"));
        testSnapshot.setCurrency("USD");
        testSnapshot.setSnapshotDate(LocalDate.now());
    }

    @Test
    @DisplayName("Should save snapshot successfully")
    void save_ShouldPersistSnapshot() {
        PortfolioSnapshot saved = portfolioSnapshotRepository.save(testSnapshot);

        assertThat(saved.getSnapshotId()).isNotNull();
        assertThat(saved.getTotalValue()).isEqualByComparingTo(new BigDecimal("10000.00"));
    }

    @Test
    @DisplayName("Should find snapshot by ID")
    void findById_WhenExists_ShouldReturnSnapshot() {
        PortfolioSnapshot persisted = entityManager.persistFlushFind(testSnapshot);

        Optional<PortfolioSnapshot> found = portfolioSnapshotRepository.findById(persisted.getSnapshotId());

        assertThat(found).isPresent();
        assertThat(found.get().getTotalValue()).isEqualByComparingTo(new BigDecimal("10000.00"));
    }

    @Test
    @DisplayName("Should find snapshots by portfolio ID ordered by date descending")
    void findByPortfolioIdOrderBySnapshotDateDesc_ShouldReturnOrderedSnapshots() {
        PortfolioSnapshot snapshot1 = new PortfolioSnapshot();
        snapshot1.setPortfolioId(testPortfolio.getPortfolioId());
        snapshot1.setTotalValue(new BigDecimal("10000.00"));
        snapshot1.setSnapshotDate(LocalDate.now().minusDays(10));

        PortfolioSnapshot snapshot2 = new PortfolioSnapshot();
        snapshot2.setPortfolioId(testPortfolio.getPortfolioId());
        snapshot2.setTotalValue(new BigDecimal("10500.00"));
        snapshot2.setSnapshotDate(LocalDate.now().minusDays(5));

        PortfolioSnapshot snapshot3 = new PortfolioSnapshot();
        snapshot3.setPortfolioId(testPortfolio.getPortfolioId());
        snapshot3.setTotalValue(new BigDecimal("11000.00"));
        snapshot3.setSnapshotDate(LocalDate.now());

        entityManager.persist(snapshot1);
        entityManager.persist(snapshot2);
        entityManager.persist(snapshot3);
        entityManager.flush();

        List<PortfolioSnapshot> snapshots = portfolioSnapshotRepository
                .findByPortfolioIdOrderBySnapshotDateDesc(testPortfolio.getPortfolioId());

        assertThat(snapshots).hasSize(3);
        // Should be ordered by date descending (most recent first)
        assertThat(snapshots.get(0).getTotalValue()).isEqualByComparingTo(new BigDecimal("11000.00"));
        assertThat(snapshots.get(1).getTotalValue()).isEqualByComparingTo(new BigDecimal("10500.00"));
        assertThat(snapshots.get(2).getTotalValue()).isEqualByComparingTo(new BigDecimal("10000.00"));
    }

    @Test
    @DisplayName("Should return empty list when portfolio has no snapshots")
    void findByPortfolioIdOrderBySnapshotDateDesc_WhenNoSnapshots_ShouldReturnEmptyList() {
        List<PortfolioSnapshot> snapshots = portfolioSnapshotRepository
                .findByPortfolioIdOrderBySnapshotDateDesc(999L);

        assertThat(snapshots).isEmpty();
    }

    @Test
    @DisplayName("Should update snapshot successfully")
    void save_ShouldUpdateExistingSnapshot() {
        PortfolioSnapshot persisted = entityManager.persistFlushFind(testSnapshot);

        persisted.setTotalValue(new BigDecimal("12000.00"));
        portfolioSnapshotRepository.save(persisted);
        entityManager.flush();
        entityManager.clear();

        PortfolioSnapshot updated = entityManager.find(PortfolioSnapshot.class, persisted.getSnapshotId());

        assertThat(updated.getTotalValue()).isEqualByComparingTo(new BigDecimal("12000.00"));
    }

    @Test
    @DisplayName("Should delete snapshot successfully")
    void delete_ShouldRemoveSnapshot() {
        PortfolioSnapshot persisted = entityManager.persistFlushFind(testSnapshot);
        Long snapshotId = persisted.getSnapshotId();

        portfolioSnapshotRepository.deleteById(snapshotId);
        entityManager.flush();

        PortfolioSnapshot deleted = entityManager.find(PortfolioSnapshot.class, snapshotId);
        assertThat(deleted).isNull();
    }

    @Test
    @DisplayName("Should set default currency when not provided")
    void save_WithoutCurrency_ShouldUseDefault() {
        PortfolioSnapshot snapshotWithoutCurrency = new PortfolioSnapshot();
        snapshotWithoutCurrency.setPortfolioId(testPortfolio.getPortfolioId());
        snapshotWithoutCurrency.setTotalValue(new BigDecimal("5000.00"));
        snapshotWithoutCurrency.setSnapshotDate(LocalDate.now());

        PortfolioSnapshot saved = portfolioSnapshotRepository.save(snapshotWithoutCurrency);

        assertThat(saved.getCurrency()).isEqualTo("INR");
    }

    @Test
    @DisplayName("Should track portfolio value over time")
    void findByPortfolioIdOrderBySnapshotDateDesc_ShouldShowValueProgression() {
        // Create snapshots showing growth over 3 months
        for (int i = 90; i >= 0; i -= 30) {
            PortfolioSnapshot snapshot = new PortfolioSnapshot();
            snapshot.setPortfolioId(testPortfolio.getPortfolioId());
            snapshot.setTotalValue(new BigDecimal(10000 + (90 - i) * 100)); // Growing value
            snapshot.setSnapshotDate(LocalDate.now().minusDays(i));
            entityManager.persist(snapshot);
        }
        entityManager.flush();

        List<PortfolioSnapshot> snapshots = portfolioSnapshotRepository
                .findByPortfolioIdOrderBySnapshotDateDesc(testPortfolio.getPortfolioId());

        assertThat(snapshots).hasSize(4);
        // Most recent should have highest value
        assertThat(snapshots.get(0).getTotalValue())
                .isGreaterThan(snapshots.get(snapshots.size() - 1).getTotalValue());
    }
}

