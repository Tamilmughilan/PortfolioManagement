package com.example.portfoliobackend.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTO Unit Tests")
class DTOTest {

    @Nested
    @DisplayName("UserDTO Tests")
    class UserDTOTest {

        @Test
        @DisplayName("Should create UserDTO with all args constructor")
        void createUserDTO_WithAllArgs_ShouldSetAllFields() {
            LocalDateTime now = LocalDateTime.now();
            UserDTO dto = new UserDTO(1L, "testuser", "test@example.com", "USD", now);

            assertThat(dto.getUserId()).isEqualTo(1L);
            assertThat(dto.getUsername()).isEqualTo("testuser");
            assertThat(dto.getEmail()).isEqualTo("test@example.com");
            assertThat(dto.getDefaultCurrency()).isEqualTo("USD");
            assertThat(dto.getCreatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("Should create UserDTO with no args constructor")
        void createUserDTO_WithNoArgs_ShouldHaveNullFields() {
            UserDTO dto = new UserDTO();

            assertThat(dto.getUserId()).isNull();
            assertThat(dto.getUsername()).isNull();
        }

        @Test
        @DisplayName("Should set and get fields correctly")
        void userDTO_SettersAndGetters_ShouldWork() {
            UserDTO dto = new UserDTO();
            dto.setUserId(2L);
            dto.setUsername("newuser");
            dto.setEmail("new@example.com");
            dto.setDefaultCurrency("EUR");

            assertThat(dto.getUserId()).isEqualTo(2L);
            assertThat(dto.getUsername()).isEqualTo("newuser");
            assertThat(dto.getEmail()).isEqualTo("new@example.com");
            assertThat(dto.getDefaultCurrency()).isEqualTo("EUR");
        }

        @Test
        @DisplayName("Should implement equals correctly")
        void userDTO_Equals_ShouldWork() {
            LocalDateTime now = LocalDateTime.now();
            UserDTO dto1 = new UserDTO(1L, "user", "user@test.com", "USD", now);
            UserDTO dto2 = new UserDTO(1L, "user", "user@test.com", "USD", now);
            UserDTO dto3 = new UserDTO(2L, "other", "other@test.com", "EUR", now);

            assertThat(dto1).isEqualTo(dto2);
            assertThat(dto1).isNotEqualTo(dto3);
        }

        @Test
        @DisplayName("Should implement hashCode correctly")
        void userDTO_HashCode_ShouldBeConsistent() {
            LocalDateTime now = LocalDateTime.now();
            UserDTO dto1 = new UserDTO(1L, "user", "user@test.com", "USD", now);
            UserDTO dto2 = new UserDTO(1L, "user", "user@test.com", "USD", now);

            assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        }

        @Test
        @DisplayName("Should implement toString")
        void userDTO_ToString_ShouldNotBeNull() {
            UserDTO dto = new UserDTO(1L, "user", "user@test.com", "USD", LocalDateTime.now());

            assertThat(dto.toString()).isNotNull();
            assertThat(dto.toString()).contains("user");
        }
    }

    @Nested
    @DisplayName("PortfolioDTO Tests")
    class PortfolioDTOTest {

        @Test
        @DisplayName("Should create PortfolioDTO with all args constructor")
        void createPortfolioDTO_WithAllArgs_ShouldSetAllFields() {
            LocalDateTime now = LocalDateTime.now();
            PortfolioDTO dto = new PortfolioDTO(1L, 1L, "My Portfolio", "USD", now);

            assertThat(dto.getPortfolioId()).isEqualTo(1L);
            assertThat(dto.getUserId()).isEqualTo(1L);
            assertThat(dto.getPortfolioName()).isEqualTo("My Portfolio");
            assertThat(dto.getBaseCurrency()).isEqualTo("USD");
            assertThat(dto.getCreatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("Should create PortfolioDTO with no args constructor")
        void createPortfolioDTO_WithNoArgs_ShouldHaveNullFields() {
            PortfolioDTO dto = new PortfolioDTO();

            assertThat(dto.getPortfolioId()).isNull();
            assertThat(dto.getPortfolioName()).isNull();
        }

        @Test
        @DisplayName("Should set and get fields correctly")
        void portfolioDTO_SettersAndGetters_ShouldWork() {
            PortfolioDTO dto = new PortfolioDTO();
            dto.setPortfolioId(1L);
            dto.setUserId(2L);
            dto.setPortfolioName("Test Portfolio");
            dto.setBaseCurrency("INR");

            assertThat(dto.getPortfolioId()).isEqualTo(1L);
            assertThat(dto.getUserId()).isEqualTo(2L);
            assertThat(dto.getPortfolioName()).isEqualTo("Test Portfolio");
            assertThat(dto.getBaseCurrency()).isEqualTo("INR");
        }
    }

    @Nested
    @DisplayName("HoldingDTO Tests")
    class HoldingDTOTest {

        @Test
        @DisplayName("Should create HoldingDTO with all args constructor")
        void createHoldingDTO_WithAllArgs_ShouldSetAllFields() {
            LocalDate purchaseDate = LocalDate.now();
            HoldingDTO dto = new HoldingDTO(1L, 1L, "Apple Inc", "STOCK",
                    new BigDecimal("10"), new BigDecimal("150.00"),
                    new BigDecimal("175.00"), "USD", purchaseDate);

            assertThat(dto.getHoldingId()).isEqualTo(1L);
            assertThat(dto.getPortfolioId()).isEqualTo(1L);
            assertThat(dto.getAssetName()).isEqualTo("Apple Inc");
            assertThat(dto.getAssetType()).isEqualTo("STOCK");
            assertThat(dto.getQuantity()).isEqualByComparingTo(new BigDecimal("10"));
            assertThat(dto.getPurchasePrice()).isEqualByComparingTo(new BigDecimal("150.00"));
            assertThat(dto.getCurrentPrice()).isEqualByComparingTo(new BigDecimal("175.00"));
            assertThat(dto.getCurrency()).isEqualTo("USD");
            assertThat(dto.getPurchaseDate()).isEqualTo(purchaseDate);
        }

        @Test
        @DisplayName("Should create HoldingDTO with no args constructor")
        void createHoldingDTO_WithNoArgs_ShouldHaveNullFields() {
            HoldingDTO dto = new HoldingDTO();

            assertThat(dto.getHoldingId()).isNull();
            assertThat(dto.getAssetName()).isNull();
        }

        @Test
        @DisplayName("Should handle BigDecimal fields correctly")
        void holdingDTO_BigDecimalFields_ShouldWork() {
            HoldingDTO dto = new HoldingDTO();
            dto.setQuantity(new BigDecimal("100.5000"));
            dto.setPurchasePrice(new BigDecimal("99.99"));
            dto.setCurrentPrice(new BigDecimal("125.50"));

            assertThat(dto.getQuantity()).isEqualByComparingTo(new BigDecimal("100.5"));
            assertThat(dto.getPurchasePrice()).isEqualByComparingTo(new BigDecimal("99.99"));
            assertThat(dto.getCurrentPrice()).isEqualByComparingTo(new BigDecimal("125.5"));
        }
    }

    @Nested
    @DisplayName("PortfolioDashboardDTO Tests")
    class PortfolioDashboardDTOTest {

        @Test
        @DisplayName("Should create PortfolioDashboardDTO with all args constructor")
        void createDashboardDTO_WithAllArgs_ShouldSetAllFields() {
            LocalDateTime now = LocalDateTime.now();
            PortfolioDashboardDTO dto = new PortfolioDashboardDTO(
                    1L, "My Portfolio", "USD",
                    new BigDecimal("10000.00"),
                    Collections.emptyList(),
                    now
            );

            assertThat(dto.getPortfolioId()).isEqualTo(1L);
            assertThat(dto.getPortfolioName()).isEqualTo("My Portfolio");
            assertThat(dto.getBaseCurrency()).isEqualTo("USD");
            assertThat(dto.getTotalValue()).isEqualByComparingTo(new BigDecimal("10000.00"));
            assertThat(dto.getHoldings()).isEmpty();
            assertThat(dto.getCreatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("Should create PortfolioDashboardDTO with no args constructor")
        void createDashboardDTO_WithNoArgs_ShouldHaveNullFields() {
            PortfolioDashboardDTO dto = new PortfolioDashboardDTO();

            assertThat(dto.getPortfolioId()).isNull();
            assertThat(dto.getHoldings()).isNull();
        }

        @Test
        @DisplayName("Should set and get holdings list")
        void dashboardDTO_Holdings_ShouldWork() {
            PortfolioDashboardDTO.HoldingDetailDTO holding = new PortfolioDashboardDTO.HoldingDetailDTO(
                    1L, "Apple", "STOCK",
                    new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("150"),
                    "USD", LocalDate.now()
            );

            PortfolioDashboardDTO dto = new PortfolioDashboardDTO();
            dto.setHoldings(Arrays.asList(holding));

            assertThat(dto.getHoldings()).hasSize(1);
            assertThat(dto.getHoldings().get(0).getAssetName()).isEqualTo("Apple");
        }
    }

    @Nested
    @DisplayName("HoldingDetailDTO Tests")
    class HoldingDetailDTOTest {

        @Test
        @DisplayName("Should calculate gain/loss correctly in constructor")
        void holdingDetailDTO_Constructor_ShouldCalculateGainLoss() {
            PortfolioDashboardDTO.HoldingDetailDTO dto = new PortfolioDashboardDTO.HoldingDetailDTO(
                    1L, "Apple", "STOCK",
                    new BigDecimal("10"), // quantity
                    new BigDecimal("100"), // purchase price
                    new BigDecimal("150"), // current price
                    "USD", LocalDate.now()
            );

            // totalInvested = 10 * 100 = 1000
            assertThat(dto.getTotalInvested()).isEqualByComparingTo(new BigDecimal("1000"));
            // currentValue = 10 * 150 = 1500
            assertThat(dto.getCurrentValue()).isEqualByComparingTo(new BigDecimal("1500"));
            // gainLoss = 1500 - 1000 = 500
            assertThat(dto.getGainLoss()).isEqualByComparingTo(new BigDecimal("500"));
            // gainLossPercentage = 500 / 1000 * 100 = 50%
            assertThat(dto.getGainLossPercentage()).isEqualByComparingTo(new BigDecimal("50"));
        }

        @Test
        @DisplayName("Should calculate negative gain/loss (loss) correctly")
        void holdingDetailDTO_Constructor_ShouldCalculateLoss() {
            PortfolioDashboardDTO.HoldingDetailDTO dto = new PortfolioDashboardDTO.HoldingDetailDTO(
                    1L, "Losing Stock", "STOCK",
                    new BigDecimal("10"), // quantity
                    new BigDecimal("150"), // purchase price
                    new BigDecimal("100"), // current price (dropped)
                    "USD", LocalDate.now()
            );

            // totalInvested = 10 * 150 = 1500
            assertThat(dto.getTotalInvested()).isEqualByComparingTo(new BigDecimal("1500"));
            // currentValue = 10 * 100 = 1000
            assertThat(dto.getCurrentValue()).isEqualByComparingTo(new BigDecimal("1000"));
            // gainLoss = 1000 - 1500 = -500
            assertThat(dto.getGainLoss()).isEqualByComparingTo(new BigDecimal("-500"));
            // gainLossPercentage = -500 / 1500 * 100 = -33.33%
            assertThat(dto.getGainLossPercentage()).isEqualByComparingTo(new BigDecimal("-33.33"));
        }

        @Test
        @DisplayName("Should handle zero investment")
        void holdingDetailDTO_ZeroInvestment_ShouldReturnZeroPercentage() {
            PortfolioDashboardDTO.HoldingDetailDTO dto = new PortfolioDashboardDTO.HoldingDetailDTO(
                    1L, "Free Stock", "STOCK",
                    new BigDecimal("10"),
                    BigDecimal.ZERO, // purchase price = 0
                    new BigDecimal("50"),
                    "USD", LocalDate.now()
            );

            assertThat(dto.getTotalInvested()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(dto.getGainLossPercentage()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should create with no args constructor")
        void holdingDetailDTO_NoArgsConstructor_ShouldWork() {
            PortfolioDashboardDTO.HoldingDetailDTO dto = new PortfolioDashboardDTO.HoldingDetailDTO();

            assertThat(dto.getHoldingId()).isNull();
            assertThat(dto.getAssetName()).isNull();
        }

        @Test
        @DisplayName("Should set and get allocation")
        void holdingDetailDTO_Allocation_ShouldWork() {
            PortfolioDashboardDTO.HoldingDetailDTO dto = new PortfolioDashboardDTO.HoldingDetailDTO();
            dto.setAllocation(new BigDecimal("25.50"));

            assertThat(dto.getAllocation()).isEqualByComparingTo(new BigDecimal("25.50"));
        }

        @Test
        @DisplayName("Should set all fields via setters")
        void holdingDetailDTO_Setters_ShouldWork() {
            PortfolioDashboardDTO.HoldingDetailDTO dto = new PortfolioDashboardDTO.HoldingDetailDTO();
            dto.setHoldingId(1L);
            dto.setAssetName("Test Asset");
            dto.setAssetType("BOND");
            dto.setQuantity(new BigDecimal("5"));
            dto.setPurchasePrice(new BigDecimal("100"));
            dto.setCurrentPrice(new BigDecimal("110"));
            dto.setCurrency("EUR");
            dto.setPurchaseDate(LocalDate.of(2025, 1, 1));
            dto.setTotalInvested(new BigDecimal("500"));
            dto.setCurrentValue(new BigDecimal("550"));
            dto.setGainLoss(new BigDecimal("50"));
            dto.setGainLossPercentage(new BigDecimal("10"));

            assertThat(dto.getHoldingId()).isEqualTo(1L);
            assertThat(dto.getAssetName()).isEqualTo("Test Asset");
            assertThat(dto.getAssetType()).isEqualTo("BOND");
            assertThat(dto.getQuantity()).isEqualByComparingTo(new BigDecimal("5"));
            assertThat(dto.getPurchasePrice()).isEqualByComparingTo(new BigDecimal("100"));
            assertThat(dto.getCurrentPrice()).isEqualByComparingTo(new BigDecimal("110"));
            assertThat(dto.getCurrency()).isEqualTo("EUR");
            assertThat(dto.getPurchaseDate()).isEqualTo(LocalDate.of(2025, 1, 1));
            assertThat(dto.getTotalInvested()).isEqualByComparingTo(new BigDecimal("500"));
            assertThat(dto.getCurrentValue()).isEqualByComparingTo(new BigDecimal("550"));
            assertThat(dto.getGainLoss()).isEqualByComparingTo(new BigDecimal("50"));
            assertThat(dto.getGainLossPercentage()).isEqualByComparingTo(new BigDecimal("10"));
        }
    }
}

