package com.example.portfoliobackend.repository;

import com.example.portfoliobackend.entity.Holding;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("HoldingRepository Integration Tests")
class HoldingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HoldingRepository holdingRepository;

    private User testUser;
    private Portfolio testPortfolio;
    private Holding testHolding;

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

        testHolding = new Holding();
        testHolding.setPortfolioId(testPortfolio.getPortfolioId());
        testHolding.setAssetName("Apple Inc");
        testHolding.setAssetType("STOCK");
        testHolding.setQuantity(new BigDecimal("10.0000"));
        testHolding.setPurchasePrice(new BigDecimal("150.00"));
        testHolding.setCurrentPrice(new BigDecimal("175.00"));
        testHolding.setCurrency("USD");
        testHolding.setPurchaseDate(LocalDate.now().minusDays(30));
    }

    @Test
    @DisplayName("Should save holding successfully")
    void save_ShouldPersistHolding() {
        Holding saved = holdingRepository.save(testHolding);

        assertThat(saved.getHoldingId()).isNotNull();
        assertThat(saved.getAssetName()).isEqualTo("Apple Inc");
    }

    @Test
    @DisplayName("Should find holding by ID")
    void findById_WhenExists_ShouldReturnHolding() {
        Holding persisted = entityManager.persistFlushFind(testHolding);

        Optional<Holding> found = holdingRepository.findById(persisted.getHoldingId());

        assertThat(found).isPresent();
        assertThat(found.get().getAssetName()).isEqualTo("Apple Inc");
    }

    @Test
    @DisplayName("Should find holdings by portfolio ID")
    void findByPortfolioId_ShouldReturnPortfolioHoldings() {
        Holding holding1 = new Holding();
        holding1.setPortfolioId(testPortfolio.getPortfolioId());
        holding1.setAssetName("Apple");
        holding1.setAssetType("STOCK");
        holding1.setQuantity(new BigDecimal("10"));
        holding1.setPurchasePrice(new BigDecimal("150"));
        holding1.setCurrentPrice(new BigDecimal("175"));
        holding1.setPurchaseDate(LocalDate.now());

        Holding holding2 = new Holding();
        holding2.setPortfolioId(testPortfolio.getPortfolioId());
        holding2.setAssetName("Google");
        holding2.setAssetType("STOCK");
        holding2.setQuantity(new BigDecimal("5"));
        holding2.setPurchasePrice(new BigDecimal("100"));
        holding2.setCurrentPrice(new BigDecimal("120"));
        holding2.setPurchaseDate(LocalDate.now());

        entityManager.persist(holding1);
        entityManager.persist(holding2);
        entityManager.flush();

        List<Holding> holdings = holdingRepository.findByPortfolioId(testPortfolio.getPortfolioId());

        assertThat(holdings).hasSize(2);
        assertThat(holdings).extracting(Holding::getAssetName)
                .containsExactlyInAnyOrder("Apple", "Google");
    }

    @Test
    @DisplayName("Should return empty list when portfolio has no holdings")
    void findByPortfolioId_WhenNoHoldings_ShouldReturnEmptyList() {
        List<Holding> holdings = holdingRepository.findByPortfolioId(999L);

        assertThat(holdings).isEmpty();
    }

    @Test
    @DisplayName("Should update holding successfully")
    void save_ShouldUpdateExistingHolding() {
        Holding persisted = entityManager.persistFlushFind(testHolding);

        persisted.setCurrentPrice(new BigDecimal("200.00"));
        persisted.setQuantity(new BigDecimal("15.0000"));
        holdingRepository.save(persisted);
        entityManager.flush();
        entityManager.clear();

        Holding updated = entityManager.find(Holding.class, persisted.getHoldingId());

        assertThat(updated.getCurrentPrice()).isEqualByComparingTo(new BigDecimal("200.00"));
        assertThat(updated.getQuantity()).isEqualByComparingTo(new BigDecimal("15.0000"));
    }

    @Test
    @DisplayName("Should delete holding successfully")
    void delete_ShouldRemoveHolding() {
        Holding persisted = entityManager.persistFlushFind(testHolding);
        Long holdingId = persisted.getHoldingId();

        holdingRepository.deleteById(holdingId);
        entityManager.flush();

        Holding deleted = entityManager.find(Holding.class, holdingId);
        assertThat(deleted).isNull();
    }

    @Test
    @DisplayName("Should store BigDecimal precision correctly")
    void save_ShouldMaintainBigDecimalPrecision() {
        testHolding.setQuantity(new BigDecimal("123.4567"));
        testHolding.setPurchasePrice(new BigDecimal("99.99"));

        Holding saved = holdingRepository.save(testHolding);
        entityManager.flush();
        entityManager.clear();

        Holding found = entityManager.find(Holding.class, saved.getHoldingId());

        assertThat(found.getQuantity()).isEqualByComparingTo(new BigDecimal("123.4567"));
        assertThat(found.getPurchasePrice()).isEqualByComparingTo(new BigDecimal("99.99"));
    }
}