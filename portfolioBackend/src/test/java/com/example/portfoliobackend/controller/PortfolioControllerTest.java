package com.example.portfoliobackend.controller;

import com.example.portfoliobackend.dto.HoldingDTO;
import com.example.portfoliobackend.dto.PortfolioDTO;
import com.example.portfoliobackend.dto.PortfolioSnapshotDTO;
import com.example.portfoliobackend.dto.PortfolioTargetDTO;
import com.example.portfoliobackend.entity.Holding;
import com.example.portfoliobackend.entity.Portfolio;
import com.example.portfoliobackend.entity.PortfolioSnapshot;
import com.example.portfoliobackend.entity.PortfolioTarget;
import com.example.portfoliobackend.service.ForecastService;
import com.example.portfoliobackend.service.PortfolioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PortfolioController.class)
@DisplayName("PortfolioController Integration Tests")
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PortfolioService portfolioService;

    @MockitoBean
    private ForecastService forecastService;

    private Portfolio testPortfolio;
    private Holding testHolding;
    private PortfolioTarget testTarget;
    private PortfolioSnapshot testSnapshot;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        testPortfolio = new Portfolio();
        testPortfolio.setPortfolioId(1L);
        testPortfolio.setUserId(1L);
        testPortfolio.setPortfolioName("Test Portfolio");
        testPortfolio.setBaseCurrency("USD");
        testPortfolio.setCreatedAt(now);

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
        testHolding.setTargetValue(null); // Explicitly set to null

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
        @DisplayName("GET /api/portfolios - Should return all portfolios")
        void getAllPortfolios_ShouldReturnList() throws Exception {
            List<Portfolio> portfolios = Arrays.asList(testPortfolio);
            when(portfolioService.getAllPortfolios()).thenReturn(portfolios);

            mockMvc.perform(get("/api/portfolios"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].portfolioName", is("Test Portfolio")))
                    .andExpect(jsonPath("$[0].portfolioId", is(1)))
                    .andExpect(jsonPath("$[0].userId", is(1)))
                    .andExpect(jsonPath("$[0].baseCurrency", is("USD")));
        }

        @Test
        @DisplayName("GET /api/portfolios/user/{userId} - Should return user portfolios")
        void getPortfoliosByUser_ShouldReturnUserPortfolios() throws Exception {
            List<Portfolio> portfolios = Arrays.asList(testPortfolio);
            when(portfolioService.getPortfoliosByUserId(1L)).thenReturn(portfolios);

            mockMvc.perform(get("/api/portfolios/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].userId", is(1)));
        }

        @Test
        @DisplayName("GET /api/portfolios/{id} - Should return portfolio when exists")
        void getPortfolioById_WhenExists_ShouldReturnPortfolio() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);

            mockMvc.perform(get("/api/portfolios/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.portfolioId", is(1)))
                    .andExpect(jsonPath("$.portfolioName", is("Test Portfolio")))
                    .andExpect(jsonPath("$.userId", is(1)))
                    .andExpect(jsonPath("$.baseCurrency", is("USD")));
        }

        @Test
        @DisplayName("GET /api/portfolios/{id} - Should return 404 when not exists")
        void getPortfolioById_WhenNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(get("/api/portfolios/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("POST /api/portfolios - Should create portfolio")
        void createPortfolio_ShouldReturnCreatedPortfolio() throws Exception {
            when(portfolioService.createPortfolio(any(Portfolio.class))).thenReturn(testPortfolio);
            
            PortfolioDTO portfolioDTO = new PortfolioDTO();
            portfolioDTO.setUserId(1L);
            portfolioDTO.setPortfolioName("Test Portfolio");
            portfolioDTO.setBaseCurrency("USD");

            mockMvc.perform(post("/api/portfolios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(portfolioDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.portfolioId", is(1)))
                    .andExpect(jsonPath("$.portfolioName", is("Test Portfolio")));
        }

        @Test
        @DisplayName("DELETE /api/portfolios/{id} - Should delete portfolio")
        void deletePortfolio_WhenExists_ShouldReturn204() throws Exception {
            when(portfolioService.deletePortfolio(1L)).thenReturn(true);

            mockMvc.perform(delete("/api/portfolios/1"))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("Holding CRUD Tests")
    class HoldingCrudTests {

        @Test
        @DisplayName("GET /api/portfolios/{id}/holdings - Should return holdings")
        void getHoldings_ShouldReturnHoldingsList() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            List<Holding> holdings = Arrays.asList(testHolding);
            when(portfolioService.getHoldingsByPortfolioId(1L)).thenReturn(holdings);

            mockMvc.perform(get("/api/portfolios/1/holdings"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].assetName", is("Apple Inc")));
        }

        @Test
        @DisplayName("GET /api/portfolios/{id}/holdings - Should return 404 when portfolio not exists")
        void getHoldings_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(get("/api/portfolios/999/holdings"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("POST /api/portfolios/{id}/holdings - Should add holding")
        void addHolding_ShouldReturnCreatedHolding() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(portfolioService.addHolding(any(Holding.class))).thenReturn(testHolding);

            HoldingDTO holdingDTO = new HoldingDTO();
            holdingDTO.setAssetName("Apple Inc");
            holdingDTO.setAssetType("STOCK");
            holdingDTO.setQuantity(new BigDecimal("10"));
            holdingDTO.setPurchasePrice(new BigDecimal("150.00"));
            holdingDTO.setCurrentPrice(new BigDecimal("175.00"));
            holdingDTO.setCurrency("USD");

            mockMvc.perform(post("/api/portfolios/1/holdings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(holdingDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.assetName", is("Apple Inc")))
                    .andExpect(jsonPath("$.assetType", is("STOCK")));
        }

        @Test
        @DisplayName("DELETE /api/portfolios/{id}/holdings/{holdingId} - Should delete holding")
        void deleteHolding_WhenExists_ShouldReturn204() throws Exception {
            when(portfolioService.getHoldingById(1L)).thenReturn(testHolding);
            when(portfolioService.deleteHolding(1L)).thenReturn(true);

            mockMvc.perform(delete("/api/portfolios/1/holdings/1"))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("Target CRUD Tests")
    class TargetCrudTests {

        @Test
        @DisplayName("GET /api/portfolios/{id}/targets - Should return targets")
        void getTargets_ShouldReturnTargetsList() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            List<PortfolioTarget> targets = Arrays.asList(testTarget);
            when(portfolioService.getTargetsByPortfolioId(1L)).thenReturn(targets);

            mockMvc.perform(get("/api/portfolios/1/targets"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].assetType", is("STOCK")));
        }

        @Test
        @DisplayName("POST /api/portfolios/{id}/targets - Should add target")
        void addTarget_ShouldReturnCreatedTarget() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            when(portfolioService.addTarget(any(PortfolioTarget.class))).thenReturn(testTarget);

            PortfolioTargetDTO targetDTO = new PortfolioTargetDTO();
            targetDTO.setAssetType("STOCK");
            targetDTO.setTargetPercentage(new BigDecimal("60.00"));

            mockMvc.perform(post("/api/portfolios/1/targets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(targetDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.targetPercentage").value(60.00))
                    .andExpect(jsonPath("$.assetType", is("STOCK")));
        }
    }

    @Nested
    @DisplayName("Snapshot Tests")
    class SnapshotTests {

        @Test
        @DisplayName("GET /api/portfolios/{id}/snapshots - Should return snapshots")
        void getSnapshots_ShouldReturnSnapshotsList() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            List<PortfolioSnapshot> snapshots = Arrays.asList(testSnapshot);
            when(portfolioService.getSnapshotsByPortfolioId(1L)).thenReturn(snapshots);

            mockMvc.perform(get("/api/portfolios/1/snapshots"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].totalValue").value(10000.00))
                    .andExpect(jsonPath("$[0].currency", is("USD")));
        }

        @Test
        @DisplayName("POST /api/portfolios/{id}/snapshots/refresh - Should refresh snapshots")
        void refreshSnapshots_ShouldReturnUpdatedSnapshots() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            List<PortfolioSnapshot> snapshots = Arrays.asList(testSnapshot);
            when(portfolioService.refreshAndGetSnapshots(eq(1L), isNull())).thenReturn(snapshots);

            mockMvc.perform(post("/api/portfolios/1/snapshots/refresh"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].totalValue").value(10000.00));
        }
        
        @Test
        @DisplayName("POST /api/portfolios/{id}/snapshots/refresh - Should refresh snapshots with currency")
        void refreshSnapshots_WithCurrency_ShouldReturnUpdatedSnapshots() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            List<PortfolioSnapshot> snapshots = Arrays.asList(testSnapshot);
            when(portfolioService.refreshAndGetSnapshots(eq(1L), eq("EUR"))).thenReturn(snapshots);

            mockMvc.perform(post("/api/portfolios/1/snapshots/refresh")
                            .param("currency", "EUR"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
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
        }
        
        @Test
        @DisplayName("GET /api/portfolios/{id}/total-value - Should return 404 when portfolio not exists")
        void getTotalValue_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(get("/api/portfolios/999/total-value"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /api/portfolios/{id}/asset-types - Should return asset types")
        void getAssetTypes_ShouldReturnTypesList() throws Exception {
            when(portfolioService.getPortfolioById(1L)).thenReturn(testPortfolio);
            List<String> assetTypes = Arrays.asList("STOCK", "BOND");
            when(portfolioService.listAssetTypes(1L)).thenReturn(assetTypes);

            mockMvc.perform(get("/api/portfolios/1/asset-types"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$", containsInAnyOrder("STOCK", "BOND")));
        }
        
        @Test
        @DisplayName("GET /api/portfolios/{id}/asset-types - Should return 404 when portfolio not exists")
        void getAssetTypes_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(get("/api/portfolios/999/asset-types"))
                    .andExpect(status().isNotFound());
        }
    }
    
    @Nested
    @DisplayName("Update and Delete Tests")
    class UpdateDeleteTests {
        
        @Test
        @DisplayName("PUT /api/portfolios/{id} - Should update portfolio")
        void updatePortfolio_ShouldReturnUpdatedPortfolio() throws Exception {
            when(portfolioService.updatePortfolio(eq(1L), any(Portfolio.class))).thenReturn(testPortfolio);
            
            PortfolioDTO portfolioDTO = new PortfolioDTO();
            portfolioDTO.setUserId(1L);
            portfolioDTO.setPortfolioName("Updated Portfolio");
            portfolioDTO.setBaseCurrency("EUR");

            mockMvc.perform(put("/api/portfolios/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(portfolioDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.portfolioId", is(1)));
        }
        
        @Test
        @DisplayName("PUT /api/portfolios/{id} - Should return 404 when portfolio not exists")
        void updatePortfolio_WhenNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.updatePortfolio(eq(999L), any(Portfolio.class))).thenReturn(null);
            
            PortfolioDTO portfolioDTO = new PortfolioDTO();
            portfolioDTO.setUserId(1L);
            portfolioDTO.setPortfolioName("Updated Portfolio");

            mockMvc.perform(put("/api/portfolios/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(portfolioDTO)))
                    .andExpect(status().isNotFound());
        }
        
        @Test
        @DisplayName("DELETE /api/portfolios/{id} - Should return 404 when portfolio not exists")
        void deletePortfolio_WhenNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.deletePortfolio(999L)).thenReturn(false);

            mockMvc.perform(delete("/api/portfolios/999"))
                    .andExpect(status().isNotFound());
        }
        
        @Test
        @DisplayName("GET /api/portfolios/{id}/holdings - Should return 404 when portfolio not exists")
        void getHoldings_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(get("/api/portfolios/999/holdings"))
                    .andExpect(status().isNotFound());
        }
        
        @Test
        @DisplayName("POST /api/portfolios/{id}/holdings - Should return 404 when portfolio not exists")
        void addHolding_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);
            
            HoldingDTO holdingDTO = new HoldingDTO();
            holdingDTO.setAssetName("Test Asset");

            mockMvc.perform(post("/api/portfolios/999/holdings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(holdingDTO)))
                    .andExpect(status().isNotFound());
        }
        
        @Test
        @DisplayName("DELETE /api/portfolios/{id}/holdings/{holdingId} - Should return 404 when holding not exists")
        void deleteHolding_WhenNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getHoldingById(999L)).thenReturn(null);

            mockMvc.perform(delete("/api/portfolios/1/holdings/999"))
                    .andExpect(status().isNotFound());
        }
        
        @Test
        @DisplayName("GET /api/portfolios/{id}/targets - Should return 404 when portfolio not exists")
        void getTargets_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(get("/api/portfolios/999/targets"))
                    .andExpect(status().isNotFound());
        }
        
        @Test
        @DisplayName("POST /api/portfolios/{id}/targets - Should return 404 when portfolio not exists")
        void addTarget_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);
            
            PortfolioTargetDTO targetDTO = new PortfolioTargetDTO();
            targetDTO.setAssetType("STOCK");
            targetDTO.setTargetPercentage(new BigDecimal("60.00"));

            mockMvc.perform(post("/api/portfolios/999/targets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(targetDTO)))
                    .andExpect(status().isNotFound());
        }
        
        @Test
        @DisplayName("GET /api/portfolios/{id}/snapshots - Should return 404 when portfolio not exists")
        void getSnapshots_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(get("/api/portfolios/999/snapshots"))
                    .andExpect(status().isNotFound());
        }
        
        @Test
        @DisplayName("POST /api/portfolios/{id}/snapshots/refresh - Should return 404 when portfolio not exists")
        void refreshSnapshots_WhenPortfolioNotExists_ShouldReturn404() throws Exception {
            when(portfolioService.getPortfolioById(999L)).thenReturn(null);

            mockMvc.perform(post("/api/portfolios/999/snapshots/refresh"))
                    .andExpect(status().isNotFound());
        }
    }
}
