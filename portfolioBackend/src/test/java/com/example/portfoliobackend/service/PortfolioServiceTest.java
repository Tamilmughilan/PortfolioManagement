package com.example.portfoliobackend.service;

import com.example.portfoliobackend.entity.Holding;
import com.example.portfoliobackend.entity.Portfolio;
import com.example.portfoliobackend.entity.PortfolioSnapshot;
import com.example.portfoliobackend.entity.PortfolioTarget;
import com.example.portfoliobackend.repository.HoldingRepository;
import com.example.portfoliobackend.repository.PortfolioRepository;
import com.example.portfoliobackend.repository.PortfolioSnapshotRepository;
import com.example.portfoliobackend.repository.PortfolioTargetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PortfolioService Unit Tests")
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private PortfolioTargetRepository portfolioTargetRepository;

    @Mock
    private PortfolioSnapshotRepository portfolioSnapshotRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    private Portfolio testPortfolio;
    private Holding testHolding;
    private PortfolioTarget testTarget;
    private PortfolioSnapshot testSnapshot;

    @BeforeEach
    void setUp() {
        testPortfolio = new Portfolio();
        testPortfolio.setPortfolioId(1L);
        testPortfolio.setUserId(1L);
        testPortfolio.setPortfolioName("Test Portfolio");
        testPortfolio.setBaseCurrency("USD");
        testPortfolio.setCreatedAt(LocalDateTime.now());

        testHolding = new Holding();
        testHolding.setHoldingId(1L);
        testHolding.setPortfolioId(1L);
        testHolding.setAssetName("Apple Inc");
        testHolding.setAssetType("STOCK");
        testHolding.setQuantity(new BigDecimal("10"));
        testHolding.setPurchasePrice(new BigDecimal("150.00"));
        testHolding.setCurrentPrice(new BigDecimal("175.00"));
        testHolding.setCurrency("USD");
        testHolding.setPurchaseDate(LocalDate.now().minusDays(30));

        testTarget = new PortfolioTarget();
        testTarget.setTargetId(1L);
        testTarget.setPortfolioId(1L);
        testTarget.setAssetType("STOCK");
        testTarget.setTargetPercentage(new BigDecimal("60.00"));

        testSnapshot = new PortfolioSnapshot();
        testSnapshot.setSnapshotId(1L);
        testSnapshot.setPortfolioId(1L);
        testSnapshot.setTotalValue(new BigDecimal("10000.00"));
        testSnapshot.setCurrency("USD");
        testSnapshot.setSnapshotDate(LocalDate.now());
    }

    @Nested
    @DisplayName("Portfolio CRUD Tests")
    class PortfolioCrudTests {

        @Test
        @DisplayName("Should return all portfolios")
        void getAllPortfolios_ShouldReturnAllPortfolios() {
            when(portfolioRepository.findAll()).thenReturn(Arrays.asList(testPortfolio));

            List<Portfolio> result = portfolioService.getAllPortfolios();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getPortfolioName()).isEqualTo("Test Portfolio");
            verify(portfolioRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return portfolios by user ID")
        void getPortfoliosByUserId_ShouldReturnUserPortfolios() {
            when(portfolioRepository.findByUserId(1L)).thenReturn(Arrays.asList(testPortfolio));

            List<Portfolio> result = portfolioService.getPortfoliosByUserId(1L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getUserId()).isEqualTo(1L);
            verify(portfolioRepository, times(1)).findByUserId(1L);
        }

        @Test
        @DisplayName("Should return portfolio by ID when exists")
        void getPortfolioById_WhenExists_ShouldReturnPortfolio() {
            when(portfolioRepository.findById(1L)).thenReturn(Optional.of(testPortfolio));

            Portfolio result = portfolioService.getPortfolioById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getPortfolioId()).isEqualTo(1L);
            verify(portfolioRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should return null when portfolio not found")
        void getPortfolioById_WhenNotExists_ShouldReturnNull() {
            when(portfolioRepository.findById(anyLong())).thenReturn(Optional.empty());

            Portfolio result = portfolioService.getPortfolioById(999L);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should create portfolio with default currency")
        void createPortfolio_WithoutCurrency_ShouldSetDefaultCurrency() {
            Portfolio newPortfolio = new Portfolio();
            newPortfolio.setUserId(1L);
            newPortfolio.setPortfolioName("New Portfolio");

            when(portfolioRepository.save(any(Portfolio.class))).thenAnswer(invocation -> {
                Portfolio saved = invocation.getArgument(0);
                saved.setPortfolioId(1L);
                return saved;
            });

            Portfolio result = portfolioService.createPortfolio(newPortfolio);

            assertThat(result.getBaseCurrency()).isEqualTo("INR");
            verify(portfolioRepository, times(1)).save(any(Portfolio.class));
        }

        @Test
        @DisplayName("Should update portfolio when exists")
        void updatePortfolio_WhenExists_ShouldUpdateAndReturn() {
            Portfolio updatedData = new Portfolio();
            updatedData.setPortfolioName("Updated Portfolio");

            when(portfolioRepository.findById(1L)).thenReturn(Optional.of(testPortfolio));
            when(portfolioRepository.save(any(Portfolio.class))).thenReturn(testPortfolio);

            Portfolio result = portfolioService.updatePortfolio(1L, updatedData);

            assertThat(result).isNotNull();
            assertThat(result.getPortfolioName()).isEqualTo("Updated Portfolio");
            verify(portfolioRepository, times(1)).save(any(Portfolio.class));
        }

        @Test
        @DisplayName("Should return null when updating non-existent portfolio")
        void updatePortfolio_WhenNotExists_ShouldReturnNull() {
            when(portfolioRepository.findById(999L)).thenReturn(Optional.empty());

            Portfolio result = portfolioService.updatePortfolio(999L, new Portfolio());

            assertThat(result).isNull();
            verify(portfolioRepository, never()).save(any(Portfolio.class));
        }

        @Test
        @DisplayName("Should delete portfolio when exists")
        void deletePortfolio_WhenExists_ShouldReturnTrue() {
            when(portfolioRepository.existsById(1L)).thenReturn(true);
            doNothing().when(portfolioRepository).deleteById(1L);

            boolean result = portfolioService.deletePortfolio(1L);

            assertThat(result).isTrue();
            verify(portfolioRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should return false when deleting non-existent portfolio")
        void deletePortfolio_WhenNotExists_ShouldReturnFalse() {
            when(portfolioRepository.existsById(999L)).thenReturn(false);

            boolean result = portfolioService.deletePortfolio(999L);

            assertThat(result).isFalse();
            verify(portfolioRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("Holding CRUD Tests")
    class HoldingCrudTests {

        @Test
        @DisplayName("Should return holdings by portfolio ID")
        void getHoldingsByPortfolioId_ShouldReturnHoldings() {
            when(holdingRepository.findByPortfolioId(1L)).thenReturn(Arrays.asList(testHolding));

            List<Holding> result = portfolioService.getHoldingsByPortfolioId(1L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAssetName()).isEqualTo("Apple Inc");
            verify(holdingRepository, times(1)).findByPortfolioId(1L);
        }

        @Test
        @DisplayName("Should add holding with default currency")
        void addHolding_WithoutCurrency_ShouldSetDefaultCurrency() {
            Holding newHolding = new Holding();
            newHolding.setPortfolioId(1L);
            newHolding.setAssetName("Google");
            newHolding.setAssetType("STOCK");
            newHolding.setQuantity(new BigDecimal("5"));
            newHolding.setPurchasePrice(new BigDecimal("100"));
            newHolding.setCurrentPrice(new BigDecimal("110"));
            newHolding.setPurchaseDate(LocalDate.now());

            when(holdingRepository.save(any(Holding.class))).thenAnswer(invocation -> {
                Holding saved = invocation.getArgument(0);
                saved.setHoldingId(1L);
                return saved;
            });

            Holding result = portfolioService.addHolding(newHolding);

            assertThat(result.getCurrency()).isEqualTo("INR");
            verify(holdingRepository, times(1)).save(any(Holding.class));
        }

        @Test
        @DisplayName("Should update holding when exists")
        void updateHolding_WhenExists_ShouldUpdateAndReturn() {
            Holding updatedData = new Holding();
            updatedData.setCurrentPrice(new BigDecimal("200.00"));

            when(holdingRepository.findById(1L)).thenReturn(Optional.of(testHolding));
            when(holdingRepository.save(any(Holding.class))).thenReturn(testHolding);

            Holding result = portfolioService.updateHolding(1L, updatedData);

            assertThat(result).isNotNull();
            assertThat(result.getCurrentPrice()).isEqualTo(new BigDecimal("200.00"));
            verify(holdingRepository, times(1)).save(any(Holding.class));
        }

        @Test
        @DisplayName("Should delete holding when exists")
        void deleteHolding_WhenExists_ShouldReturnTrue() {
            when(holdingRepository.existsById(1L)).thenReturn(true);
            doNothing().when(holdingRepository).deleteById(1L);

            boolean result = portfolioService.deleteHolding(1L);

            assertThat(result).isTrue();
            verify(holdingRepository, times(1)).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("Target CRUD Tests")
    class TargetCrudTests {

        @Test
        @DisplayName("Should return targets by portfolio ID")
        void getTargetsByPortfolioId_ShouldReturnTargets() {
            when(portfolioTargetRepository.findByPortfolioId(1L)).thenReturn(Arrays.asList(testTarget));

            List<PortfolioTarget> result = portfolioService.getTargetsByPortfolioId(1L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAssetType()).isEqualTo("STOCK");
            verify(portfolioTargetRepository, times(1)).findByPortfolioId(1L);
        }

        @Test
        @DisplayName("Should add target")
        void addTarget_ShouldSaveAndReturnTarget() {
            when(portfolioTargetRepository.save(any(PortfolioTarget.class))).thenReturn(testTarget);

            PortfolioTarget result = portfolioService.addTarget(testTarget);

            assertThat(result).isNotNull();
            assertThat(result.getTargetPercentage()).isEqualTo(new BigDecimal("60.00"));
            verify(portfolioTargetRepository, times(1)).save(any(PortfolioTarget.class));
        }
    }

    @Nested
    @DisplayName("Snapshot Tests")
    class SnapshotTests {

        @Test
        @DisplayName("Should return snapshots by portfolio ID ordered by date desc")
        void getSnapshotsByPortfolioId_ShouldReturnOrderedSnapshots() {
            when(portfolioSnapshotRepository.findByPortfolioIdOrderBySnapshotDateDesc(1L))
                    .thenReturn(Arrays.asList(testSnapshot));

            List<PortfolioSnapshot> result = portfolioService.getSnapshotsByPortfolioId(1L);

            assertThat(result).hasSize(1);
            verify(portfolioSnapshotRepository, times(1)).findByPortfolioIdOrderBySnapshotDateDesc(1L);
        }

        @Test
        @DisplayName("Should record snapshot with default currency when not provided")
        void recordSnapshot_WithoutCurrency_ShouldSetDefaultCurrency() {
            when(portfolioSnapshotRepository.save(any(PortfolioSnapshot.class))).thenAnswer(invocation -> {
                PortfolioSnapshot saved = invocation.getArgument(0);
                saved.setSnapshotId(1L);
                return saved;
            });

            PortfolioSnapshot result = portfolioService.recordSnapshot(1L, new BigDecimal("10000"), null);

            assertThat(result.getCurrency()).isEqualTo("INR");
            assertThat(result.getSnapshotDate()).isEqualTo(LocalDate.now());
            verify(portfolioSnapshotRepository, times(1)).save(any(PortfolioSnapshot.class));
        }
    }

    @Nested
    @DisplayName("Calculation Tests")
    class CalculationTests {

        @Test
        @DisplayName("Should calculate total portfolio value correctly")
        void calculateTotalValue_ShouldReturnCorrectSum() {
            Holding holding1 = new Holding();
            holding1.setQuantity(new BigDecimal("10"));
            holding1.setCurrentPrice(new BigDecimal("100"));

            Holding holding2 = new Holding();
            holding2.setQuantity(new BigDecimal("5"));
            holding2.setCurrentPrice(new BigDecimal("200"));

            when(holdingRepository.findByPortfolioId(1L)).thenReturn(Arrays.asList(holding1, holding2));

            BigDecimal result = portfolioService.calculateTotalValue(1L);

            // 10*100 + 5*200 = 1000 + 1000 = 2000
            assertThat(result).isEqualByComparingTo(new BigDecimal("2000"));
        }

        @Test
        @DisplayName("Should return zero when no holdings")
        void calculateTotalValue_WhenNoHoldings_ShouldReturnZero() {
            when(holdingRepository.findByPortfolioId(1L)).thenReturn(Collections.emptyList());

            BigDecimal result = portfolioService.calculateTotalValue(1L);

            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should list distinct asset types")
        void listAssetTypes_ShouldReturnDistinctTypes() {
            Holding holding1 = new Holding();
            holding1.setAssetType("STOCK");

            Holding holding2 = new Holding();
            holding2.setAssetType("BOND");

            Holding holding3 = new Holding();
            holding3.setAssetType("STOCK");

            when(holdingRepository.findByPortfolioId(1L)).thenReturn(Arrays.asList(holding1, holding2, holding3));

            List<String> result = portfolioService.listAssetTypes(1L);

            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyInAnyOrder("STOCK", "BOND");
        }
    }
}

