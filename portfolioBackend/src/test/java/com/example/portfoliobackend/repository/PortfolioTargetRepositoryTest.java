package com.example.portfoliobackend.repository;

import com.example.portfoliobackend.entity.Portfolio;
import com.example.portfoliobackend.entity.PortfolioTarget;
import com.example.portfoliobackend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("PortfolioTargetRepository Integration Tests")
class PortfolioTargetRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PortfolioTargetRepository portfolioTargetRepository;

    private User testUser;
    private Portfolio testPortfolio;
    private PortfolioTarget testTarget;

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

        testTarget = new PortfolioTarget();
        testTarget.setPortfolioId(testPortfolio.getPortfolioId());
        testTarget.setAssetType("STOCK");
        testTarget.setTargetPercentage(new BigDecimal("60.00"));
    }

    @Test
    @DisplayName("Should save target successfully")
    void save_ShouldPersistTarget() {
        PortfolioTarget saved = portfolioTargetRepository.save(testTarget);

        assertThat(saved.getTargetId()).isNotNull();
        assertThat(saved.getAssetType()).isEqualTo("STOCK");
        assertThat(saved.getTargetPercentage()).isEqualByComparingTo(new BigDecimal("60.00"));
    }

    @Test
    @DisplayName("Should find target by ID")
    void findById_WhenExists_ShouldReturnTarget() {
        PortfolioTarget persisted = entityManager.persistFlushFind(testTarget);

        Optional<PortfolioTarget> found = portfolioTargetRepository.findById(persisted.getTargetId());

        assertThat(found).isPresent();
        assertThat(found.get().getAssetType()).isEqualTo("STOCK");
    }

    @Test
    @DisplayName("Should find targets by portfolio ID")
    void findByPortfolioId_ShouldReturnPortfolioTargets() {
        PortfolioTarget stockTarget = new PortfolioTarget();
        stockTarget.setPortfolioId(testPortfolio.getPortfolioId());
        stockTarget.setAssetType("STOCK");
        stockTarget.setTargetPercentage(new BigDecimal("60.00"));

        PortfolioTarget bondTarget = new PortfolioTarget();
        bondTarget.setPortfolioId(testPortfolio.getPortfolioId());
        bondTarget.setAssetType("BOND");
        bondTarget.setTargetPercentage(new BigDecimal("30.00"));

        PortfolioTarget cashTarget = new PortfolioTarget();
        cashTarget.setPortfolioId(testPortfolio.getPortfolioId());
        cashTarget.setAssetType("CASH");
        cashTarget.setTargetPercentage(new BigDecimal("10.00"));

        entityManager.persist(stockTarget);
        entityManager.persist(bondTarget);
        entityManager.persist(cashTarget);
        entityManager.flush();

        List<PortfolioTarget> targets = portfolioTargetRepository.findByPortfolioId(testPortfolio.getPortfolioId());

        assertThat(targets).hasSize(3);
        assertThat(targets).extracting(PortfolioTarget::getAssetType)
                .containsExactlyInAnyOrder("STOCK", "BOND", "CASH");
    }

    @Test
    @DisplayName("Should return empty list when portfolio has no targets")
    void findByPortfolioId_WhenNoTargets_ShouldReturnEmptyList() {
        List<PortfolioTarget> targets = portfolioTargetRepository.findByPortfolioId(999L);

        assertThat(targets).isEmpty();
    }

    @Test
    @DisplayName("Should update target successfully")
    void save_ShouldUpdateExistingTarget() {
        PortfolioTarget persisted = entityManager.persistFlushFind(testTarget);

        persisted.setTargetPercentage(new BigDecimal("70.00"));
        portfolioTargetRepository.save(persisted);
        entityManager.flush();
        entityManager.clear();

        PortfolioTarget updated = entityManager.find(PortfolioTarget.class, persisted.getTargetId());

        assertThat(updated.getTargetPercentage()).isEqualByComparingTo(new BigDecimal("70.00"));
    }

    @Test
    @DisplayName("Should delete target successfully")
    void delete_ShouldRemoveTarget() {
        PortfolioTarget persisted = entityManager.persistFlushFind(testTarget);
        Long targetId = persisted.getTargetId();

        portfolioTargetRepository.deleteById(targetId);
        entityManager.flush();

        PortfolioTarget deleted = entityManager.find(PortfolioTarget.class, targetId);
        assertThat(deleted).isNull();
    }

    @Test
    @DisplayName("Should store percentage with correct precision")
    void save_ShouldMaintainPercentagePrecision() {
        testTarget.setTargetPercentage(new BigDecimal("33.33"));

        PortfolioTarget saved = portfolioTargetRepository.save(testTarget);
        entityManager.flush();
        entityManager.clear();

        PortfolioTarget found = entityManager.find(PortfolioTarget.class, saved.getTargetId());

        assertThat(found.getTargetPercentage()).isEqualByComparingTo(new BigDecimal("33.33"));
    }
}

