package com.example.portfoliobackend.controller;

import com.example.portfoliobackend.dto.PortfolioDashboardDTO;
import com.example.portfoliobackend.entity.Holding;
import com.example.portfoliobackend.entity.Portfolio;
import com.example.portfoliobackend.entity.PortfolioSnapshot;
import com.example.portfoliobackend.entity.PortfolioTarget;
import com.example.portfoliobackend.service.PortfolioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PortfolioController.class)
@ActiveProfiles("test")
@DisplayName("PortfolioController Integration Tests")
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PortfolioService portfolioService;

    private Portfolio testPortfolio;
    private Portfolio testPortfolio2;
    private Holding testHolding;
    private Holding testHolding2;
    private PortfolioTarget testTarget;
    private PortfolioTarget testTarget2;
    private PortfolioSnapshot testSnapshot;
    private PortfolioSnapshot testSnapshot2;

    @BeforeEach
    void setUp() {
        testPortfolio = new Portfolio();
        testPortfolio.setPortfolioId(1L);
        testPortfolio.setUserId(1L);
        testPortfolio.setPortfolioName("Test Portfolio");
        testPortfolio.setBaseCurrency("USD");
        testPortfolio.setCreatedAt(LocalDateTime.now());

        testPortfolio2 = new Portfolio();
        testPortfolio2.setPortfolioId(2L);
        testPortfolio2.setUserId(1L);
        testPortfolio2.setPortfolioName("Second Portfolio");
        testPortfolio2.setBaseCurrency("INR");
        testPortfolio2.setCreatedAt(LocalDateTime.now());

        testHolding = new Holding();
        testHolding.setHoldingId(1L);
        testHolding.setPortfolioId(1L);
        testHolding.setAssetName("Apple Inc");
        testHolding.setAssetType("STOCK");
        testHolding.setQuantity(new BigDecimal("10"));
        testHolding.setPurchasePrice(new BigDecimal("150.00"));
        testHolding.setCurrentPrice(new BigDecimal("175.00"));
        testHolding.setCurrency("USD");
        testHolding.setPurchaseDate(LocalDate.now());

        testHolding2 = new Holding();
        testHolding2.setHoldingId(2L);
        testHolding2.setPortfolioId(1L);
        testHolding2.setAssetName("Google");
        testHolding2.setAssetType("STOCK");
        testHolding2.setQuantity(new BigDecimal("5"));
        testHolding2.setPurchasePrice(new BigDecimal("100.00"));
        testHolding2.setCurrentPrice(new BigDecimal("120.00"));
        testHolding2.setCurrency("USD");
        testHolding2.setPurchaseDate(LocalDate.now());

        testTarget = new PortfolioTarget();
        testTarget.setTargetId(1L);
        testTarget.setPortfolioId(1L);
        testTarget.setAssetType("STOCK");
        testTarget.setTargetPercentage(new BigDecimal("60.00"));

        testTarget2 = new PortfolioTarget();
        testTarget2.setTargetId(2L);
        testTarget2.setPortfolioId(1L);
        testTarget2.setAssetType("BOND");
        testTarget2.setTargetPercentage(new BigDecimal("40.00"));

        testSnapshot = new PortfolioSnapshot();
        testSnapshot.setSnapshotId(1L);
        testSnapshot.setPortfolioId(1L);
        testSnapshot.setTotalValue(new BigDecimal("10000.00"));
        testSnapshot.setCurrency("USD");
        testSnapshot.setSnapshotDate(LocalDate.now());

        testSnapshot2 = new PortfolioSnapshot();
        testSnapshot2.setSnapshotId(2L);
        testSnapshot2.setPortfolioId(1L);
        testSnapshot2.setTotalValue(new BigDecimal("11000.00"));
        testSnapshot2.setCurrency("USD");
        testSnapshot2.setSnapshotDate(LocalDate.now().minusDays(1));
    }

    @Nested
    @DisplayName("Portfolio CRUD Tests")
    class PortfolioCrudTests {

        @Test
        @DisplayName("GET /api/portfolios - Should return all portfolios")
        void getAllPortfolios_ShouldReturnList() throws Exception {
            List<Portfolio> portfolios = Arrays.asList(testPortfolio, testPortfolio2);
            when(portfolioService.getAllPortfolios()).thenReturn(portfolios);

            mockMvc.perform(get("/api/portfolios"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].portfolioName", is("Test Portfolio")))
                    .andExpect(jsonPath("$[1].portfolioName", is("Second Portfolio")));

            verify(portfolioService, times(1)).getAllPortfolios();
        }

        @Test
        @DisplayName("GET /api/portfolios - Should return empty list")
        void getAllPortfolios_WhenEmpty_ShouldReturnEmptyList() throws Exception {
            when(portfolioService.getAllPortfolios()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/portfolios"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(portfolioService, times(1)).getAllPortfolios();
        }

        @Test
        @DisplayName("GET /api/portfolios/user/{userId} - Should return user portfolios")
        void getPortfoliosByUser_ShouldReturnUserPortfolios() throws Exception {
            List<Portfolio> portfolios = Arrays.asList(testPortfolio, testPortfolio2);
            when(portfolioService.getPortfoliosByUserId(1L)).thenReturn(portfolios);

            mockMvc.perform(get("/api/portfolios/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].userId", is(1)))
                    .andExpect(jsonPath("$[1].userId", is(1)));

            verify(portfolioService, times(1)).getPortfoliosByUserId(1L);
        }

        @Test
        @DisplayName("GET /api/portfolios/user/{userId} - Should return empty for user with no portfolios")
        void getPortfoliosByUser_WhenNoPortfolios_ShouldReturnEmptyList() throws Exception {
            when(portfolioService.getPortfoliosByUserId(999L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/portfolios/user/999"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("GET /api/portfolios/{id} - Should return portfolio when exists")
        void getPortfolioById_WhenExists_ShouldReturnPortfolio() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);

            mockMvc.perform(get("/api/portfolios/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.portfolioId", is(1)))
                    .andExpect(jsonPath("$.portfolioName", is("Test Portfolio")))
                    .andExpect(jsonPath("$.baseCurrency", is("USD")));

            verify(portfolioService, times(1)).getPortfolioById(1L);
        }

        @Test
        @DisplayName("GET /api/portfolios/{id} - Should return 404 when not exists")
        void getPortfolioById_WhenNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(get("/api/portfolios/999"))
                    .andExpect(status().isNotFound());

            verify(portfolioService, times(1)).getPortfolioById(999L);
        }

        @Test
        @DisplayName("POST /api/portfolios - Should create portfolio")
        void createPortfolio_ShouldReturnCreatedPortfolio() throws Exception {
            when(portfolioService.createPortfolio(any(Portfolio.class))).thenReturn(testPortfolio);

            mockMvc.perform(post("/api/portfolios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testPortfolio)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.portfolioId", is(1)))
                    .andExpect(jsonPath("$.portfolioName", is("Test Portfolio")));

            verify(portfolioService, times(1)).createPortfolio(any(Portfolio.class));
        }

        @Test
        @DisplayName("PUT /api/portfolios/{id} - Should update portfolio")
        void updatePortfolio_WhenExists_ShouldReturnUpdated() throws Exception {
            Portfolio updated = new Portfolio();
            updated.setPortfolioId(1L);
            updated.setPortfolioName("Updated Portfolio");
            updated.setBaseCurrency("EUR");

            when(portfolioService.updatePortfolio(eq(1L), any(Portfolio.class))).thenReturn(updated);

            mockMvc.perform(put("/api/portfolios/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updated)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.portfolioName", is("Updated Portfolio")))
                    .andExpect(jsonPath("$.baseCurrency", is("EUR")));

            verify(portfolioService, times(1)).updatePortfolio(eq(1L), any(Portfolio.class));
        }

        @Test
        @DisplayName("PUT /api/portfolios/{id} - Should return 404 when not exists")
        void updatePortfolio_WhenNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.updatePortfolio(eq(999L), any(Portfolio.class))).thenReturn(null);

            mockMvc.perform(put("/api/portfolios/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testPortfolio)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("DELETE /api/portfolios/{id} - Should delete portfolio")
        void deletePortfolio_WhenExists_ShouldReturn204() throws Exception {
            when(portfolioService.deletePortfolio(1L)).thenReturn(true);

            mockMvc.perform(delete("/api/portfolios/1"))
                    .andExpect(status().isNoContent());

            verify(portfolioService, times(1)).deletePortfolio(1L);
        }

        @Test
        @DisplayName("DELETE /api/portfolios/{id} - Should return 404 when not exists")
        void deletePortfolio_WhenNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.deletePortfolio(999L)).thenReturn(false);

            mockMvc.perform(delete("/api/portfolios/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Holding CRUD Tests")
    class HoldingCrudTests {

        @Test
        @DisplayName("GET /api/portfolios/{id}/holdings - Should return holdings")
        void getHoldings_ShouldReturnHoldingsList() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            List<Holding> holdings = Arrays.asList(testHolding, testHolding2);
            when(portfolioService.getHoldingsByPortfolioId(1L)).thenReturn(holdings);

            mockMvc.perform(get("/api/portfolios/1/holdings"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].assetName", is("Apple Inc")))
                    .andExpect(jsonPath("$[1].assetName", is("Google")));

            verify(portfolioService, times(1)).getPortfolioById(1L);
            verify(portfolioService, times(1)).getHoldingsByPortfolioId(1L);
        }

        @Test
        @DisplayName("GET /api/portfolios/{id}/holdings - Should return empty list")
        void getHoldings_WhenEmpty_ShouldReturnEmptyList() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(portfolioService.getHoldingsByPortfolioId(1L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/portfolios/1/holdings"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("GET /api/portfolios/{id}/holdings - Should return 404 when portfolio not exists")
        void getHoldings_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(get("/api/portfolios/999/holdings"))
                    .andExpect(status().isNotFound());

            verify(portfolioService, never()).getHoldingsByPortfolioId(999L);
        }

        @Test
        @DisplayName("GET /api/portfolios/{id}/holdings/{holdingId} - Should return holding")
        void getHolding_WhenExists_ShouldReturnHolding() throws Exception {
            when(portfolioService.getHoldingById(1L)).thenReturn(testHolding);

            mockMvc.perform(get("/api/portfolios/1/holdings/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.holdingId", is(1)))
                    .andExpect(jsonPath("$.assetName", is("Apple Inc")));
        }

        @Test
        @DisplayName("GET /api/portfolios/{id}/holdings/{holdingId} - Should return 404 when not exists")
        void getHolding_WhenNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getHoldingById(999L)).thenReturn(null);

            mockMvc.perform(get("/api/portfolios/1/holdings/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /api/portfolios/{id}/holdings/{holdingId} - Should return 404 when wrong portfolio")
        void getHolding_WhenWrongPortfolio_ShouldReturn404() throws Exception {
            testHolding.setPortfolioId(2L); // Different portfolio
            when(portfolioService.getHoldingById(1L)).thenReturn(testHolding);

            mockMvc.perform(get("/api/portfolios/1/holdings/1"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("POST /api/portfolios/{id}/holdings - Should add holding")
        void addHolding_ShouldReturnCreatedHolding() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(portfolioService.addHolding(any(Holding.class))).thenReturn(testHolding);

            mockMvc.perform(post("/api/portfolios/1/holdings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testHolding)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.assetName", is("Apple Inc")))
                    .andExpect(jsonPath("$.assetType", is("STOCK")));

            verify(portfolioService, times(1)).addHolding(any(Holding.class));
        }

        @Test
        @DisplayName("POST /api/portfolios/{id}/holdings - Should return 404 when portfolio not exists")
        void addHolding_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(post("/api/portfolios/999/holdings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testHolding)))
                    .andExpect(status().isNotFound());

            verify(portfolioService, never()).addHolding(any(Holding.class));
        }

        @Test
        @DisplayName("PUT /api/portfolios/{id}/holdings/{holdingId} - Should update holding")
        void updateHolding_ShouldReturnUpdatedHolding() throws Exception {
            Holding updated = new Holding();
            updated.setHoldingId(1L);
            updated.setPortfolioId(1L);
            updated.setAssetName("Apple Inc Updated");
            updated.setCurrentPrice(new BigDecimal("200.00"));

            when(portfolioService.getHoldingById(1L)).thenReturn(testHolding);
            when(portfolioService.updateHolding(eq(1L), any(Holding.class))).thenReturn(updated);

            mockMvc.perform(put("/api/portfolios/1/holdings/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updated)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.assetName", is("Apple Inc Updated")));
        }

        @Test
        @DisplayName("DELETE /api/portfolios/{id}/holdings/{holdingId} - Should delete holding")
        void deleteHolding_WhenExists_ShouldReturn204() throws Exception {
            when(portfolioService.getHoldingById(1L)).thenReturn(testHolding);
            when(portfolioService.deleteHolding(1L)).thenReturn(true);

            mockMvc.perform(delete("/api/portfolios/1/holdings/1"))
                    .andExpect(status().isNoContent());

            verify(portfolioService, times(1)).deleteHolding(1L);
        }

        @Test
        @DisplayName("DELETE /api/portfolios/{id}/holdings/{holdingId} - Should return 404 when not exists")
        void deleteHolding_WhenNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getHoldingById(999L)).thenReturn(null);

            mockMvc.perform(delete("/api/portfolios/1/holdings/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Target CRUD Tests")
    class TargetCrudTests {

        @Test
        @DisplayName("GET /api/portfolios/{id}/targets - Should return targets")
        void getTargets_ShouldReturnTargetsList() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            List<PortfolioTarget> targets = Arrays.asList(testTarget, testTarget2);
            when(portfolioService.getTargetsByPortfolioId(1L)).thenReturn(targets);

            mockMvc.perform(get("/api/portfolios/1/targets"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].assetType", is("STOCK")))
                    .andExpect(jsonPath("$[1].assetType", is("BOND")));

            verify(portfolioService, times(1)).getTargetsByPortfolioId(1L);
        }

        @Test
        @DisplayName("GET /api/portfolios/{id}/targets - Should return 404 when portfolio not exists")
        void getTargets_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(get("/api/portfolios/999/targets"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /api/portfolios/{id}/targets/{targetId} - Should return target")
        void getTarget_WhenExists_ShouldReturnTarget() throws Exception {
            when(portfolioService.getTargetById(1L)).thenReturn(testTarget);

            mockMvc.perform(get("/api/portfolios/1/targets/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.targetId", is(1)))
                    .andExpect(jsonPath("$.assetType", is("STOCK")));
        }

        @Test
        @DisplayName("POST /api/portfolios/{id}/targets - Should add target")
        void addTarget_ShouldReturnCreatedTarget() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(portfolioService.addTarget(any(PortfolioTarget.class))).thenReturn(testTarget);

            mockMvc.perform(post("/api/portfolios/1/targets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testTarget)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.assetType", is("STOCK")))
                    .andExpect(jsonPath("$.targetPercentage", closeTo(60.00, 0.01)));

            verify(portfolioService, times(1)).addTarget(any(PortfolioTarget.class));
        }

        @Test
        @DisplayName("PUT /api/portfolios/{id}/targets/{targetId} - Should update target")
        void updateTarget_ShouldReturnUpdatedTarget() throws Exception {
            PortfolioTarget updated = new PortfolioTarget();
            updated.setTargetId(1L);
            updated.setPortfolioId(1L);
            updated.setAssetType("STOCK");
            updated.setTargetPercentage(new BigDecimal("70.00"));

            when(portfolioService.getTargetById(1L)).thenReturn(testTarget);
            when(portfolioService.updateTarget(eq(1L), any(PortfolioTarget.class))).thenReturn(updated);

            mockMvc.perform(put("/api/portfolios/1/targets/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updated)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.targetPercentage", closeTo(70.00, 0.01)));
        }

        @Test
        @DisplayName("DELETE /api/portfolios/{id}/targets/{targetId} - Should delete target")
        void deleteTarget_WhenExists_ShouldReturn204() throws Exception {
            when(portfolioService.getTargetById(1L)).thenReturn(testTarget);
            when(portfolioService.deleteTarget(1L)).thenReturn(true);

            mockMvc.perform(delete("/api/portfolios/1/targets/1"))
                    .andExpect(status().isNoContent());

            verify(portfolioService, times(1)).deleteTarget(1L);
        }
    }

    @Nested
    @DisplayName("Snapshot Tests")
    class SnapshotTests {

        @Test
        @DisplayName("GET /api/portfolios/{id}/snapshots - Should return snapshots")
        void getSnapshots_ShouldReturnSnapshotsList() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            List<PortfolioSnapshot> snapshots = Arrays.asList(testSnapshot, testSnapshot2);
            when(portfolioService.getSnapshotsByPortfolioId(1L)).thenReturn(snapshots);

            mockMvc.perform(get("/api/portfolios/1/snapshots"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].totalValue", closeTo(10000.00, 0.01)))
                    .andExpect(jsonPath("$[1].totalValue", closeTo(11000.00, 0.01)));

            verify(portfolioService, times(1)).getSnapshotsByPortfolioId(1L);
        }

        @Test
        @DisplayName("GET /api/portfolios/{id}/snapshots - Should return 404 when portfolio not exists")
        void getSnapshots_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(get("/api/portfolios/999/snapshots"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /api/portfolios/{id}/snapshots/{snapshotId} - Should return snapshot")
        void getSnapshot_WhenExists_ShouldReturnSnapshot() throws Exception {
            when(portfolioService.getSnapshotById(1L)).thenReturn(testSnapshot);

            mockMvc.perform(get("/api/portfolios/1/snapshots/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.snapshotId", is(1)))
                    .andExpect(jsonPath("$.totalValue", closeTo(10000.00, 0.01)));
        }

        @Test
        @DisplayName("POST /api/portfolios/{id}/snapshots/refresh - Should refresh snapshots")
        void refreshSnapshots_ShouldReturnUpdatedSnapshots() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            List<PortfolioSnapshot> snapshots = Arrays.asList(testSnapshot);
            when(portfolioService.refreshAndGetSnapshots(eq(1L), isNull())).thenReturn(snapshots);

            mockMvc.perform(post("/api/portfolios/1/snapshots/refresh"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));

            verify(portfolioService, times(1)).refreshAndGetSnapshots(eq(1L), isNull());
        }

        @Test
        @DisplayName("POST /api/portfolios/{id}/snapshots/refresh - Should refresh with currency")
        void refreshSnapshots_WithCurrency_ShouldReturnUpdatedSnapshots() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            List<PortfolioSnapshot> snapshots = Arrays.asList(testSnapshot);
            when(portfolioService.refreshAndGetSnapshots(eq(1L), eq("EUR"))).thenReturn(snapshots);

            mockMvc.perform(post("/api/portfolios/1/snapshots/refresh?currency=EUR"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));

            verify(portfolioService, times(1)).refreshAndGetSnapshots(eq(1L), eq("EUR"));
        }

        @Test
        @DisplayName("POST /api/portfolios/{id}/snapshots/refresh - Should return 404 when portfolio not exists")
        void refreshSnapshots_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(post("/api/portfolios/999/snapshots/refresh"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("DELETE /api/portfolios/{id}/snapshots/{snapshotId} - Should delete snapshot")
        void deleteSnapshot_WhenExists_ShouldReturn204() throws Exception {
            when(portfolioService.getSnapshotById(1L)).thenReturn(testSnapshot);
            when(portfolioService.deleteSnapshot(1L)).thenReturn(true);

            mockMvc.perform(delete("/api/portfolios/1/snapshots/1"))
                    .andExpect(status().isNoContent());

            verify(portfolioService, times(1)).deleteSnapshot(1L);
        }
    }

    @Nested
    @DisplayName("Utility Endpoint Tests")
    class UtilityEndpointTests {

        @Test
        @DisplayName("GET /api/portfolios/{id}/total-value - Should return total value")
        void getTotalValue_ShouldReturnValue() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("10000.00"));

            mockMvc.perform(get("/api/portfolios/1/total-value"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("10000.00"));

            verify(portfolioService, times(1)).calculateTotalValue(1L);
        }

        @Test
        @DisplayName("GET /api/portfolios/{id}/total-value - Should return 404 when portfolio not exists")
        void getTotalValue_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(get("/api/portfolios/999/total-value"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /api/portfolios/{id}/total-value - Should return zero when no holdings")
        void getTotalValue_WhenNoHoldings_ShouldReturnZero() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(portfolioService.calculateTotalValue(1L)).thenReturn(BigDecimal.ZERO);

            mockMvc.perform(get("/api/portfolios/1/total-value"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("0"));
        }

        @Test
        @DisplayName("GET /api/portfolios/{id}/asset-types - Should return asset types")
        void getAssetTypes_ShouldReturnTypesList() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            List<String> assetTypes = Arrays.asList("STOCK", "BOND", "CASH");
            when(portfolioService.listAssetTypes(1L)).thenReturn(assetTypes);

            mockMvc.perform(get("/api/portfolios/1/asset-types"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$", containsInAnyOrder("STOCK", "BOND", "CASH")));

            verify(portfolioService, times(1)).listAssetTypes(1L);
        }

        @Test
        @DisplayName("GET /api/portfolios/{id}/asset-types - Should return 404 when portfolio not exists")
        void getAssetTypes_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(get("/api/portfolios/999/asset-types"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /api/portfolios/{id}/asset-types - Should return empty list when no holdings")
        void getAssetTypes_WhenNoHoldings_ShouldReturnEmptyList() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(portfolioService.listAssetTypes(1L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/portfolios/1/asset-types"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle portfolio ID 0")
        void getPortfolio_WithZeroId_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(0L)).thenReturn(null);

            mockMvc.perform(get("/api/portfolios/0"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should handle holdings with decimal quantities")
        void getHoldings_WithDecimalQuantities_ShouldReturnCorrect() throws Exception {
            testHolding.setQuantity(new BigDecimal("10.5000"));
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(portfolioService.getHoldingsByPortfolioId(1L)).thenReturn(Arrays.asList(testHolding));

            mockMvc.perform(get("/api/portfolios/1/holdings"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].quantity", closeTo(10.5, 0.0001)));
        }
    }

    @Nested
    @DisplayName("Dashboard Tests")
    class DashboardTests {

        @Test
        @DisplayName("GET /api/portfolios/{id}/dashboard - Should return dashboard with holdings")
        void getDashboard_ShouldReturnDashboard() throws Exception {
            PortfolioDashboardDTO.HoldingDetailDTO holdingDTO = new PortfolioDashboardDTO.HoldingDetailDTO(
                    1L, "Apple Inc", "STOCK",
                    new BigDecimal("10"), new BigDecimal("150.00"), new BigDecimal("175.00"),
                    "USD", LocalDate.now()
            );
            holdingDTO.setAllocation(new BigDecimal("100.00"));

            PortfolioDashboardDTO dashboard = new PortfolioDashboardDTO(
                    1L, "Test Portfolio", "USD",
                    new BigDecimal("1750.00"),
                    Arrays.asList(holdingDTO),
                    LocalDateTime.now()
            );

            when(portfolioService.getPortfolioDashboard(1L)).thenReturn(dashboard);

            mockMvc.perform(get("/api/portfolios/1/dashboard"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.portfolioId", is(1)))
                    .andExpect(jsonPath("$.portfolioName", is("Test Portfolio")))
                    .andExpect(jsonPath("$.baseCurrency", is("USD")))
                    .andExpect(jsonPath("$.totalValue", closeTo(1750.00, 0.01)))
                    .andExpect(jsonPath("$.holdings", hasSize(1)))
                    .andExpect(jsonPath("$.holdings[0].assetName", is("Apple Inc")))
                    .andExpect(jsonPath("$.holdings[0].gainLoss", closeTo(250.00, 0.01)));

            verify(portfolioService, times(1)).getPortfolioDashboard(1L);
        }

        @Test
        @DisplayName("GET /api/portfolios/{id}/dashboard - Should return 404 when portfolio not exists")
        void getDashboard_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioDashboard(999L)).thenReturn(null);

            mockMvc.perform(get("/api/portfolios/999/dashboard"))
                    .andExpect(status().isNotFound());

            verify(portfolioService, times(1)).getPortfolioDashboard(999L);
        }

        @Test
        @DisplayName("GET /api/portfolios/{id}/dashboard - Should return dashboard with empty holdings")
        void getDashboard_WithNoHoldings_ShouldReturnEmptyHoldings() throws Exception {
            PortfolioDashboardDTO dashboard = new PortfolioDashboardDTO(
                    1L, "Test Portfolio", "USD",
                    BigDecimal.ZERO,
                    Collections.emptyList(),
                    LocalDateTime.now()
            );

            when(portfolioService.getPortfolioDashboard(1L)).thenReturn(dashboard);

            mockMvc.perform(get("/api/portfolios/1/dashboard"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.portfolioId", is(1)))
                    .andExpect(jsonPath("$.totalValue", closeTo(0.0, 0.01)))
                    .andExpect(jsonPath("$.holdings", hasSize(0)));
        }

        @Test
        @DisplayName("GET /api/portfolios/{id}/dashboard - Should return dashboard with multiple holdings")
        void getDashboard_WithMultipleHoldings_ShouldReturnAll() throws Exception {
            PortfolioDashboardDTO.HoldingDetailDTO holding1 = new PortfolioDashboardDTO.HoldingDetailDTO(
                    1L, "Apple Inc", "STOCK",
                    new BigDecimal("10"), new BigDecimal("150.00"), new BigDecimal("175.00"),
                    "USD", LocalDate.now()
            );
            holding1.setAllocation(new BigDecimal("74.47"));

            PortfolioDashboardDTO.HoldingDetailDTO holding2 = new PortfolioDashboardDTO.HoldingDetailDTO(
                    2L, "Google", "STOCK",
                    new BigDecimal("5"), new BigDecimal("100.00"), new BigDecimal("120.00"),
                    "USD", LocalDate.now()
            );
            holding2.setAllocation(new BigDecimal("25.53"));

            PortfolioDashboardDTO dashboard = new PortfolioDashboardDTO(
                    1L, "Test Portfolio", "USD",
                    new BigDecimal("2350.00"),
                    Arrays.asList(holding1, holding2),
                    LocalDateTime.now()
            );

            when(portfolioService.getPortfolioDashboard(1L)).thenReturn(dashboard);

            mockMvc.perform(get("/api/portfolios/1/dashboard"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.holdings", hasSize(2)))
                    .andExpect(jsonPath("$.holdings[0].allocation", closeTo(74.47, 0.01)))
                    .andExpect(jsonPath("$.holdings[1].allocation", closeTo(25.53, 0.01)));
        }
    }

    @Nested
    @DisplayName("Record Snapshot Tests")
    class RecordSnapshotTests {

        @Test
        @DisplayName("POST /api/portfolios/{id}/snapshots - Should record snapshot with request body")
        void recordSnapshot_WithRequestBody_ShouldReturnCreatedSnapshot() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(portfolioService.recordSnapshot(eq(1L), any(BigDecimal.class), any(String.class)))
                    .thenReturn(testSnapshot);

            String requestBody = "{\"totalValue\": 15000.00, \"currency\": \"EUR\"}";

            mockMvc.perform(post("/api/portfolios/1/snapshots")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.snapshotId", is(1)))
                    .andExpect(jsonPath("$.totalValue", closeTo(10000.00, 0.01)));

            verify(portfolioService, times(1)).recordSnapshot(eq(1L), any(BigDecimal.class), eq("EUR"));
        }

        @Test
        @DisplayName("POST /api/portfolios/{id}/snapshots - Should calculate value when not provided")
        void recordSnapshot_WithoutTotalValue_ShouldCalculate() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("12000.00"));
            when(portfolioService.recordSnapshot(eq(1L), eq(new BigDecimal("12000.00")), isNull()))
                    .thenReturn(testSnapshot);

            String requestBody = "{}";

            mockMvc.perform(post("/api/portfolios/1/snapshots")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated());

            verify(portfolioService, times(1)).calculateTotalValue(1L);
        }

        @Test
        @DisplayName("POST /api/portfolios/{id}/snapshots - Should return 404 when portfolio not exists")
        void recordSnapshot_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            String requestBody = "{\"totalValue\": 15000.00}";

            mockMvc.perform(post("/api/portfolios/999/snapshots")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isNotFound());

            verify(portfolioService, never()).recordSnapshot(any(), any(), any());
        }

        @Test
        @DisplayName("POST /api/portfolios/{id}/snapshots - Should use default currency when not provided")
        void recordSnapshot_WithoutCurrency_ShouldUseDefault() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(portfolioService.recordSnapshot(eq(1L), any(BigDecimal.class), isNull()))
                    .thenReturn(testSnapshot);

            String requestBody = "{\"totalValue\": 15000.00}";

            mockMvc.perform(post("/api/portfolios/1/snapshots")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated());

            verify(portfolioService, times(1)).recordSnapshot(eq(1L), any(BigDecimal.class), isNull());
        }

        @Test
        @DisplayName("PUT /api/portfolios/{id}/snapshots/{snapshotId} - Should update snapshot")
        void updateSnapshot_ShouldReturnUpdatedSnapshot() throws Exception {
            PortfolioSnapshot updated = new PortfolioSnapshot();
            updated.setSnapshotId(1L);
            updated.setPortfolioId(1L);
            updated.setTotalValue(new BigDecimal("12000.00"));
            updated.setCurrency("EUR");
            updated.setSnapshotDate(LocalDate.now());

            when(portfolioService.getSnapshotById(1L)).thenReturn(testSnapshot);
            when(portfolioService.updateSnapshot(eq(1L), any(PortfolioSnapshot.class))).thenReturn(updated);

            mockMvc.perform(put("/api/portfolios/1/snapshots/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updated)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalValue", closeTo(12000.00, 0.01)))
                    .andExpect(jsonPath("$.currency", is("EUR")));

            verify(portfolioService, times(1)).updateSnapshot(eq(1L), any(PortfolioSnapshot.class));
        }

        @Test
        @DisplayName("PUT /api/portfolios/{id}/snapshots/{snapshotId} - Should return 404 when snapshot not exists")
        void updateSnapshot_WhenNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getSnapshotById(999L)).thenReturn(null);

            mockMvc.perform(put("/api/portfolios/1/snapshots/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testSnapshot)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("PUT /api/portfolios/{id}/snapshots/{snapshotId} - Should return 404 when wrong portfolio")
        void updateSnapshot_WhenWrongPortfolio_ShouldReturn404() throws Exception {
            testSnapshot.setPortfolioId(2L); // Different portfolio
            when(portfolioService.getSnapshotById(1L)).thenReturn(testSnapshot);

            mockMvc.perform(put("/api/portfolios/1/snapshots/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testSnapshot)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("DELETE /api/portfolios/{id}/snapshots/{snapshotId} - Should return 404 when wrong portfolio")
        void deleteSnapshot_WhenWrongPortfolio_ShouldReturn404() throws Exception {
            testSnapshot.setPortfolioId(2L); // Different portfolio
            when(portfolioService.getSnapshotById(1L)).thenReturn(testSnapshot);

            mockMvc.perform(delete("/api/portfolios/1/snapshots/1"))
                    .andExpect(status().isNotFound());
        }
    }
}
