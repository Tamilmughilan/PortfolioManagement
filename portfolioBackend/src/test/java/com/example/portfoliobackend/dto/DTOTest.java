package com.example.portfoliobackend.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTO Classes Unit Tests")
class DTOTest {

    @Nested
    @DisplayName("PortfolioDTO Tests")
    class PortfolioDTOTests {
        @Test
        void testNoArgsConstructor() {
            PortfolioDTO dto = new PortfolioDTO();
            assertThat(dto).isNotNull();
            assertThat(dto.getPortfolioId()).isNull();
            assertThat(dto.getUserId()).isNull();
            assertThat(dto.getPortfolioName()).isNull();
            assertThat(dto.getBaseCurrency()).isNull();
            assertThat(dto.getCreatedAt()).isNull();
        }

        @Test
        void testAllArgsConstructor() {
            LocalDateTime now = LocalDateTime.now();
            PortfolioDTO dto = new PortfolioDTO(1L, 2L, "Test Portfolio", "USD", now);
            assertThat(dto.getPortfolioId()).isEqualTo(1L);
            assertThat(dto.getUserId()).isEqualTo(2L);
            assertThat(dto.getPortfolioName()).isEqualTo("Test Portfolio");
            assertThat(dto.getBaseCurrency()).isEqualTo("USD");
            assertThat(dto.getCreatedAt()).isEqualTo(now);
        }

        @Test
        void testGettersAndSetters() {
            PortfolioDTO dto = new PortfolioDTO();
            LocalDateTime now = LocalDateTime.now();
            
            dto.setPortfolioId(1L);
            dto.setUserId(2L);
            dto.setPortfolioName("My Portfolio");
            dto.setBaseCurrency("EUR");
            dto.setCreatedAt(now);

            assertThat(dto.getPortfolioId()).isEqualTo(1L);
            assertThat(dto.getUserId()).isEqualTo(2L);
            assertThat(dto.getPortfolioName()).isEqualTo("My Portfolio");
            assertThat(dto.getBaseCurrency()).isEqualTo("EUR");
            assertThat(dto.getCreatedAt()).isEqualTo(now);
        }

        @Test
        void testWithNullValues() {
            PortfolioDTO dto = new PortfolioDTO();
            dto.setPortfolioId(null);
            dto.setUserId(null);
            dto.setPortfolioName(null);
            dto.setBaseCurrency(null);
            dto.setCreatedAt(null);

            assertThat(dto.getPortfolioId()).isNull();
            assertThat(dto.getUserId()).isNull();
            assertThat(dto.getPortfolioName()).isNull();
            assertThat(dto.getBaseCurrency()).isNull();
            assertThat(dto.getCreatedAt()).isNull();
        }

        @Test
        void testWithEmptyString() {
            PortfolioDTO dto = new PortfolioDTO();
            dto.setPortfolioName("");
            dto.setBaseCurrency("");

            assertThat(dto.getPortfolioName()).isEmpty();
            assertThat(dto.getBaseCurrency()).isEmpty();
        }
    }

    @Nested
    @DisplayName("HoldingDTO Tests")
    class HoldingDTOTests {
        @Test
        void testNoArgsConstructor() {
            HoldingDTO dto = new HoldingDTO();
            assertThat(dto).isNotNull();
            assertThat(dto.getHoldingId()).isNull();
        }

        @Test
        void testAllArgsConstructor() {
            LocalDate date = LocalDate.now();
            HoldingDTO dto = new HoldingDTO(1L, 2L, "Apple", "STOCK", 
                    new BigDecimal("10"), new BigDecimal("150"), new BigDecimal("175"), 
                    "USD", date, new BigDecimal("2000"));
            
            assertThat(dto.getHoldingId()).isEqualTo(1L);
            assertThat(dto.getPortfolioId()).isEqualTo(2L);
            assertThat(dto.getAssetName()).isEqualTo("Apple");
            assertThat(dto.getAssetType()).isEqualTo("STOCK");
            assertThat(dto.getQuantity()).isEqualByComparingTo(new BigDecimal("10"));
            assertThat(dto.getTargetValue()).isEqualByComparingTo(new BigDecimal("2000"));
        }

        @Test
        void testGettersAndSetters() {
            HoldingDTO dto = new HoldingDTO();
            LocalDate date = LocalDate.now();
            
            dto.setHoldingId(1L);
            dto.setPortfolioId(2L);
            dto.setAssetName("Tesla");
            dto.setAssetType("STOCK");
            dto.setQuantity(new BigDecimal("5"));
            dto.setPurchasePrice(new BigDecimal("200"));
            dto.setCurrentPrice(new BigDecimal("250"));
            dto.setCurrency("USD");
            dto.setPurchaseDate(date);
            dto.setTargetValue(new BigDecimal("3000"));

            assertThat(dto.getHoldingId()).isEqualTo(1L);
            assertThat(dto.getAssetName()).isEqualTo("Tesla");
            assertThat(dto.getQuantity()).isEqualByComparingTo(new BigDecimal("5"));
            assertThat(dto.getTargetValue()).isEqualByComparingTo(new BigDecimal("3000"));
        }

        @Test
        void testWithNullTargetValue() {
            HoldingDTO dto = new HoldingDTO();
            dto.setTargetValue(null);
            assertThat(dto.getTargetValue()).isNull();
        }

        @Test
        void testWithZeroValues() {
            HoldingDTO dto = new HoldingDTO();
            dto.setQuantity(BigDecimal.ZERO);
            dto.setPurchasePrice(BigDecimal.ZERO);
            dto.setCurrentPrice(BigDecimal.ZERO);
            
            assertThat(dto.getQuantity()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(dto.getPurchasePrice()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(dto.getCurrentPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void testWithNegativeValues() {
            HoldingDTO dto = new HoldingDTO();
            dto.setQuantity(new BigDecimal("-5"));
            
            assertThat(dto.getQuantity()).isEqualByComparingTo(new BigDecimal("-5"));
        }
    }

    @Nested
    @DisplayName("UserDTO Tests")
    class UserDTOTests {
        @Test
        void testNoArgsConstructor() {
            UserDTO dto = new UserDTO();
            assertThat(dto).isNotNull();
        }

        @Test
        void testAllArgsConstructor() {
            LocalDateTime now = LocalDateTime.now();
            UserDTO dto = new UserDTO(1L, "testuser", "test@example.com", "USD", now);
            
            assertThat(dto.getUserId()).isEqualTo(1L);
            assertThat(dto.getUsername()).isEqualTo("testuser");
            assertThat(dto.getEmail()).isEqualTo("test@example.com");
            assertThat(dto.getDefaultCurrency()).isEqualTo("USD");
            assertThat(dto.getCreatedAt()).isEqualTo(now);
        }

        @Test
        void testGettersAndSetters() {
            UserDTO dto = new UserDTO();
            LocalDateTime now = LocalDateTime.now();
            
            dto.setUserId(1L);
            dto.setUsername("john");
            dto.setEmail("john@example.com");
            dto.setDefaultCurrency("EUR");
            dto.setCreatedAt(now);

            assertThat(dto.getUserId()).isEqualTo(1L);
            assertThat(dto.getUsername()).isEqualTo("john");
            assertThat(dto.getEmail()).isEqualTo("john@example.com");
            assertThat(dto.getDefaultCurrency()).isEqualTo("EUR");
        }

        @Test
        void testWithNullValues() {
            UserDTO dto = new UserDTO();
            dto.setUserId(null);
            dto.setUsername(null);
            dto.setEmail(null);
            dto.setDefaultCurrency(null);
            dto.setCreatedAt(null);

            assertThat(dto.getUserId()).isNull();
            assertThat(dto.getUsername()).isNull();
            assertThat(dto.getEmail()).isNull();
            assertThat(dto.getDefaultCurrency()).isNull();
            assertThat(dto.getCreatedAt()).isNull();
        }

        @Test
        void testWithEmptyStrings() {
            UserDTO dto = new UserDTO();
            dto.setUsername("");
            dto.setEmail("");
            dto.setDefaultCurrency("");

            assertThat(dto.getUsername()).isEmpty();
            assertThat(dto.getEmail()).isEmpty();
            assertThat(dto.getDefaultCurrency()).isEmpty();
        }
    }

    @Nested
    @DisplayName("PortfolioTargetDTO Tests")
    class PortfolioTargetDTOTests {
        @Test
        void testNoArgsConstructor() {
            PortfolioTargetDTO dto = new PortfolioTargetDTO();
            assertThat(dto).isNotNull();
        }

        @Test
        void testAllArgsConstructor() {
            PortfolioTargetDTO dto = new PortfolioTargetDTO(1L, 2L, "STOCK", new BigDecimal("60.00"));
            
            assertThat(dto.getTargetId()).isEqualTo(1L);
            assertThat(dto.getPortfolioId()).isEqualTo(2L);
            assertThat(dto.getAssetType()).isEqualTo("STOCK");
            assertThat(dto.getTargetPercentage()).isEqualByComparingTo(new BigDecimal("60.00"));
        }

        @Test
        void testGettersAndSetters() {
            PortfolioTargetDTO dto = new PortfolioTargetDTO();
            
            dto.setTargetId(1L);
            dto.setPortfolioId(2L);
            dto.setAssetType("BOND");
            dto.setTargetPercentage(new BigDecimal("40.00"));

            assertThat(dto.getTargetId()).isEqualTo(1L);
            assertThat(dto.getAssetType()).isEqualTo("BOND");
            assertThat(dto.getTargetPercentage()).isEqualByComparingTo(new BigDecimal("40.00"));
        }

        @Test
        void testWithZeroPercentage() {
            PortfolioTargetDTO dto = new PortfolioTargetDTO();
            dto.setTargetPercentage(BigDecimal.ZERO);
            
            assertThat(dto.getTargetPercentage()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void testWithHundredPercentage() {
            PortfolioTargetDTO dto = new PortfolioTargetDTO();
            dto.setTargetPercentage(new BigDecimal("100.00"));
            
            assertThat(dto.getTargetPercentage()).isEqualByComparingTo(new BigDecimal("100.00"));
        }
    }

    @Nested
    @DisplayName("PortfolioSnapshotDTO Tests")
    class PortfolioSnapshotDTOTests {
        @Test
        void testNoArgsConstructor() {
            PortfolioSnapshotDTO dto = new PortfolioSnapshotDTO();
            assertThat(dto).isNotNull();
        }

        @Test
        void testAllArgsConstructor() {
            LocalDate date = LocalDate.now();
            PortfolioSnapshotDTO dto = new PortfolioSnapshotDTO(1L, 2L, 
                    new BigDecimal("10000"), "USD", date);
            
            assertThat(dto.getSnapshotId()).isEqualTo(1L);
            assertThat(dto.getPortfolioId()).isEqualTo(2L);
            assertThat(dto.getTotalValue()).isEqualByComparingTo(new BigDecimal("10000"));
            assertThat(dto.getCurrency()).isEqualTo("USD");
            assertThat(dto.getSnapshotDate()).isEqualTo(date);
        }

        @Test
        void testGettersAndSetters() {
            PortfolioSnapshotDTO dto = new PortfolioSnapshotDTO();
            LocalDate date = LocalDate.now();
            
            dto.setSnapshotId(1L);
            dto.setPortfolioId(2L);
            dto.setTotalValue(new BigDecimal("15000"));
            dto.setCurrency("EUR");
            dto.setSnapshotDate(date);

            assertThat(dto.getSnapshotId()).isEqualTo(1L);
            assertThat(dto.getTotalValue()).isEqualByComparingTo(new BigDecimal("15000"));
            assertThat(dto.getCurrency()).isEqualTo("EUR");
        }

        @Test
        void testWithZeroValue() {
            PortfolioSnapshotDTO dto = new PortfolioSnapshotDTO();
            dto.setTotalValue(BigDecimal.ZERO);
            
            assertThat(dto.getTotalValue()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void testWithNullValues() {
            PortfolioSnapshotDTO dto = new PortfolioSnapshotDTO();
            dto.setSnapshotId(null);
            dto.setPortfolioId(null);
            dto.setTotalValue(null);
            dto.setCurrency(null);
            dto.setSnapshotDate(null);

            assertThat(dto.getSnapshotId()).isNull();
            assertThat(dto.getPortfolioId()).isNull();
            assertThat(dto.getTotalValue()).isNull();
            assertThat(dto.getCurrency()).isNull();
            assertThat(dto.getSnapshotDate()).isNull();
        }
    }

    @Nested
    @DisplayName("PortfolioGoalDTO Tests")
    class PortfolioGoalDTOTests {
        @Test
        void testNoArgsConstructor() {
            PortfolioGoalDTO dto = new PortfolioGoalDTO();
            assertThat(dto).isNotNull();
        }

        @Test
        void testAllArgsConstructor() {
            LocalDate date = LocalDate.now().plusYears(5);
            LocalDateTime createdAt = LocalDateTime.now();
            PortfolioGoalDTO dto = new PortfolioGoalDTO(1L, 2L, "Dream Home", 
                    new BigDecimal("500000"), date, new BigDecimal("8.0"), createdAt);
            
            assertThat(dto.getGoalId()).isEqualTo(1L);
            assertThat(dto.getPortfolioId()).isEqualTo(2L);
            assertThat(dto.getGoalName()).isEqualTo("Dream Home");
            assertThat(dto.getTargetAmount()).isEqualByComparingTo(new BigDecimal("500000"));
            assertThat(dto.getExpectedAnnualReturn()).isEqualByComparingTo(new BigDecimal("8.0"));
        }

        @Test
        void testGettersAndSetters() {
            PortfolioGoalDTO dto = new PortfolioGoalDTO();
            LocalDate date = LocalDate.now().plusYears(10);
            LocalDateTime createdAt = LocalDateTime.now();
            
            dto.setGoalId(1L);
            dto.setPortfolioId(2L);
            dto.setGoalName("Retirement");
            dto.setTargetAmount(new BigDecimal("1000000"));
            dto.setTargetDate(date);
            dto.setExpectedAnnualReturn(new BigDecimal("7.5"));
            dto.setCreatedAt(createdAt);

            assertThat(dto.getGoalId()).isEqualTo(1L);
            assertThat(dto.getGoalName()).isEqualTo("Retirement");
            assertThat(dto.getTargetAmount()).isEqualByComparingTo(new BigDecimal("1000000"));
        }

        @Test
        void testWithZeroTargetAmount() {
            PortfolioGoalDTO dto = new PortfolioGoalDTO();
            dto.setTargetAmount(BigDecimal.ZERO);
            
            assertThat(dto.getTargetAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void testWithNullExpectedReturn() {
            PortfolioGoalDTO dto = new PortfolioGoalDTO();
            dto.setExpectedAnnualReturn(null);
            
            assertThat(dto.getExpectedAnnualReturn()).isNull();
        }

        @Test
        void testWithPastTargetDate() {
            PortfolioGoalDTO dto = new PortfolioGoalDTO();
            dto.setTargetDate(LocalDate.now().minusDays(1));
            
            assertThat(dto.getTargetDate()).isBefore(LocalDate.now());
        }
    }

    @Nested
    @DisplayName("AnalyticsDTO Tests")
    class AnalyticsDTOTests {
        @Test
        void testNoArgsConstructor() {
            AnalyticsDTO dto = new AnalyticsDTO();
            assertThat(dto).isNotNull();
        }

        @Test
        void testAllArgsConstructor() {
            Map<String, BigDecimal> allocations = new HashMap<>();
            allocations.put("STOCK", new BigDecimal("6000"));
            allocations.put("BOND", new BigDecimal("4000"));
            Map<String, BigDecimal> percentages = new HashMap<>();
            percentages.put("STOCK", new BigDecimal("60"));
            percentages.put("BOND", new BigDecimal("40"));
            Map<String, BigDecimal> drift = new HashMap<>();
            drift.put("STOCK", new BigDecimal("5"));
            drift.put("BOND", new BigDecimal("-2"));
            
            AnalyticsDTO dto = new AnalyticsDTO(
                    new BigDecimal("10000"),
                    new BigDecimal("8000"),
                    new BigDecimal("2000"),
                    allocations,
                    percentages,
                    drift
            );
            
            assertThat(dto.getTotalMarketValue()).isEqualByComparingTo(new BigDecimal("10000"));
            assertThat(dto.getTotalCost()).isEqualByComparingTo(new BigDecimal("8000"));
            assertThat(dto.getTotalGainLoss()).isEqualByComparingTo(new BigDecimal("2000"));
            assertThat(dto.getAllocationValues()).isEqualTo(allocations);
            assertThat(dto.getAllocationPercentages()).isEqualTo(percentages);
            assertThat(dto.getTargetDrift()).isEqualTo(drift);
        }

        @Test
        void testGettersAndSetters() {
            AnalyticsDTO dto = new AnalyticsDTO();
            Map<String, BigDecimal> allocations = new HashMap<>();
            allocations.put("BOND", new BigDecimal("4000"));
            Map<String, BigDecimal> percentages = new HashMap<>();
            percentages.put("BOND", new BigDecimal("40"));
            Map<String, BigDecimal> drift = new HashMap<>();
            drift.put("BOND", new BigDecimal("-2"));
            
            dto.setTotalMarketValue(new BigDecimal("15000"));
            dto.setTotalCost(new BigDecimal("12000"));
            dto.setTotalGainLoss(new BigDecimal("3000"));
            dto.setAllocationValues(allocations);
            dto.setAllocationPercentages(percentages);
            dto.setTargetDrift(drift);

            assertThat(dto.getTotalMarketValue()).isEqualByComparingTo(new BigDecimal("15000"));
            assertThat(dto.getTotalCost()).isEqualByComparingTo(new BigDecimal("12000"));
            assertThat(dto.getTotalGainLoss()).isEqualByComparingTo(new BigDecimal("3000"));
            assertThat(dto.getAllocationValues()).isEqualTo(allocations);
            assertThat(dto.getAllocationPercentages()).isEqualTo(percentages);
            assertThat(dto.getTargetDrift()).isEqualTo(drift);
        }

        @Test
        void testWithEmptyMaps() {
            AnalyticsDTO dto = new AnalyticsDTO();
            dto.setAllocationValues(new HashMap<>());
            dto.setAllocationPercentages(new HashMap<>());
            dto.setTargetDrift(new HashMap<>());

            assertThat(dto.getAllocationValues()).isEmpty();
            assertThat(dto.getAllocationPercentages()).isEmpty();
            assertThat(dto.getTargetDrift()).isEmpty();
        }

        @Test
        void testWithNullMaps() {
            AnalyticsDTO dto = new AnalyticsDTO();
            dto.setAllocationValues(null);
            dto.setAllocationPercentages(null);
            dto.setTargetDrift(null);

            assertThat(dto.getAllocationValues()).isNull();
            assertThat(dto.getAllocationPercentages()).isNull();
            assertThat(dto.getTargetDrift()).isNull();
        }

        @Test
        void testWithNegativeGainLoss() {
            AnalyticsDTO dto = new AnalyticsDTO();
            dto.setTotalMarketValue(new BigDecimal("5000"));
            dto.setTotalCost(new BigDecimal("8000"));
            dto.setTotalGainLoss(new BigDecimal("-3000"));

            assertThat(dto.getTotalGainLoss()).isEqualByComparingTo(new BigDecimal("-3000"));
        }

        @Test
        void testWithZeroValues() {
            AnalyticsDTO dto = new AnalyticsDTO();
            dto.setTotalMarketValue(BigDecimal.ZERO);
            dto.setTotalCost(BigDecimal.ZERO);
            dto.setTotalGainLoss(BigDecimal.ZERO);

            assertThat(dto.getTotalMarketValue()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(dto.getTotalCost()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(dto.getTotalGainLoss()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("AuthRequestDTO Tests")
    class AuthRequestDTOTests {
        @Test
        void testNoArgsConstructor() {
            AuthRequestDTO dto = new AuthRequestDTO();
            assertThat(dto).isNotNull();
        }

        @Test
        void testAllArgsConstructor() {
            AuthRequestDTO dto = new AuthRequestDTO("testuser", "password123");
            
            assertThat(dto.getIdentifier()).isEqualTo("testuser");
            assertThat(dto.getPassword()).isEqualTo("password123");
        }

        @Test
        void testGettersAndSetters() {
            AuthRequestDTO dto = new AuthRequestDTO();
            dto.setIdentifier("user@example.com");
            dto.setPassword("secret");

            assertThat(dto.getIdentifier()).isEqualTo("user@example.com");
            assertThat(dto.getPassword()).isEqualTo("secret");
        }

        @Test
        void testWithNullValues() {
            AuthRequestDTO dto = new AuthRequestDTO();
            dto.setIdentifier(null);
            dto.setPassword(null);

            assertThat(dto.getIdentifier()).isNull();
            assertThat(dto.getPassword()).isNull();
        }

        @Test
        void testWithEmptyStrings() {
            AuthRequestDTO dto = new AuthRequestDTO();
            dto.setIdentifier("");
            dto.setPassword("");

            assertThat(dto.getIdentifier()).isEmpty();
            assertThat(dto.getPassword()).isEmpty();
        }
    }

    @Nested
    @DisplayName("SignupRequestDTO Tests")
    class SignupRequestDTOTests {
        @Test
        void testNoArgsConstructor() {
            SignupRequestDTO dto = new SignupRequestDTO();
            assertThat(dto).isNotNull();
        }

        @Test
        void testAllArgsConstructor() {
            SignupRequestDTO dto = new SignupRequestDTO("newuser", "new@example.com", "password123", "USD");
            
            assertThat(dto.getUsername()).isEqualTo("newuser");
            assertThat(dto.getEmail()).isEqualTo("new@example.com");
            assertThat(dto.getPassword()).isEqualTo("password123");
            assertThat(dto.getDefaultCurrency()).isEqualTo("USD");
        }

        @Test
        void testGettersAndSetters() {
            SignupRequestDTO dto = new SignupRequestDTO();
            dto.setUsername("john");
            dto.setEmail("john@example.com");
            dto.setPassword("pass123");
            dto.setDefaultCurrency("EUR");

            assertThat(dto.getUsername()).isEqualTo("john");
            assertThat(dto.getEmail()).isEqualTo("john@example.com");
            assertThat(dto.getPassword()).isEqualTo("pass123");
            assertThat(dto.getDefaultCurrency()).isEqualTo("EUR");
        }

        @Test
        void testWithNullValues() {
            SignupRequestDTO dto = new SignupRequestDTO();
            dto.setUsername(null);
            dto.setEmail(null);
            dto.setPassword(null);
            dto.setDefaultCurrency(null);

            assertThat(dto.getUsername()).isNull();
            assertThat(dto.getEmail()).isNull();
            assertThat(dto.getPassword()).isNull();
            assertThat(dto.getDefaultCurrency()).isNull();
        }
    }

    @Nested
    @DisplayName("AuthResponseDTO Tests")
    class AuthResponseDTOTests {
        @Test
        void testNoArgsConstructor() {
            AuthResponseDTO dto = new AuthResponseDTO();
            assertThat(dto).isNotNull();
        }

        @Test
        void testAllArgsConstructor() {
            AuthResponseDTO dto = new AuthResponseDTO(1L, "testuser", "test@example.com", "USD", "token123");
            
            assertThat(dto.getUserId()).isEqualTo(1L);
            assertThat(dto.getUsername()).isEqualTo("testuser");
            assertThat(dto.getEmail()).isEqualTo("test@example.com");
            assertThat(dto.getDefaultCurrency()).isEqualTo("USD");
            assertThat(dto.getToken()).isEqualTo("token123");
        }

        @Test
        void testGettersAndSetters() {
            AuthResponseDTO dto = new AuthResponseDTO();
            dto.setUserId(1L);
            dto.setUsername("user");
            dto.setEmail("user@example.com");
            dto.setDefaultCurrency("INR");
            dto.setToken("token456");

            assertThat(dto.getUserId()).isEqualTo(1L);
            assertThat(dto.getToken()).isEqualTo("token456");
        }

        @Test
        void testWithNullToken() {
            AuthResponseDTO dto = new AuthResponseDTO();
            dto.setToken(null);

            assertThat(dto.getToken()).isNull();
        }
    }

    @Nested
    @DisplayName("ChatRequestDTO Tests")
    class ChatRequestDTOTests {
        @Test
        void testNoArgsConstructor() {
            ChatRequestDTO dto = new ChatRequestDTO();
            assertThat(dto).isNotNull();
        }

        @Test
        void testAllArgsConstructor() {
            ChatRequestDTO dto = new ChatRequestDTO("What is a stock?", 1L);
            
            assertThat(dto.getMessage()).isEqualTo("What is a stock?");
            assertThat(dto.getPortfolioId()).isEqualTo(1L);
        }

        @Test
        void testGettersAndSetters() {
            ChatRequestDTO dto = new ChatRequestDTO();
            dto.setMessage("Tell me about bonds");
            dto.setPortfolioId(2L);

            assertThat(dto.getMessage()).isEqualTo("Tell me about bonds");
            assertThat(dto.getPortfolioId()).isEqualTo(2L);
        }

        @Test
        void testWithNullPortfolioId() {
            ChatRequestDTO dto = new ChatRequestDTO();
            dto.setMessage("General question");
            dto.setPortfolioId(null);

            assertThat(dto.getMessage()).isEqualTo("General question");
            assertThat(dto.getPortfolioId()).isNull();
        }

        @Test
        void testWithEmptyMessage() {
            ChatRequestDTO dto = new ChatRequestDTO();
            dto.setMessage("");
            dto.setPortfolioId(1L);

            assertThat(dto.getMessage()).isEmpty();
            assertThat(dto.getPortfolioId()).isEqualTo(1L);
        }

        @Test
        void testWithNullMessage() {
            ChatRequestDTO dto = new ChatRequestDTO();
            dto.setMessage(null);
            dto.setPortfolioId(1L);

            assertThat(dto.getMessage()).isNull();
            assertThat(dto.getPortfolioId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("ChatResponseDTO Tests")
    class ChatResponseDTOTests {
        @Test
        void testNoArgsConstructor() {
            ChatResponseDTO dto = new ChatResponseDTO();
            assertThat(dto).isNotNull();
        }

        @Test
        void testAllArgsConstructor() {
            ChatResponseDTO dto = new ChatResponseDTO("This is a response");
            
            assertThat(dto.getResponse()).isEqualTo("This is a response");
        }

        @Test
        void testGettersAndSetters() {
            ChatResponseDTO dto = new ChatResponseDTO();
            dto.setResponse("Another response");

            assertThat(dto.getResponse()).isEqualTo("Another response");
        }

        @Test
        void testWithNullResponse() {
            ChatResponseDTO dto = new ChatResponseDTO();
            dto.setResponse(null);

            assertThat(dto.getResponse()).isNull();
        }

        @Test
        void testWithEmptyResponse() {
            ChatResponseDTO dto = new ChatResponseDTO();
            dto.setResponse("");

            assertThat(dto.getResponse()).isEmpty();
        }
    }

    @Nested
    @DisplayName("MarketNewsDTO Tests")
    class MarketNewsDTOTests {
        @Test
        void testNoArgsConstructor() {
            MarketNewsDTO dto = new MarketNewsDTO();
            assertThat(dto).isNotNull();
        }

        @Test
        void testAllArgsConstructor() {
            MarketNewsDTO dto = new MarketNewsDTO("Headline", "Source", "http://url.com", 
                    1234567890L, "Summary text", "http://image.com");
            
            assertThat(dto.getHeadline()).isEqualTo("Headline");
            assertThat(dto.getSource()).isEqualTo("Source");
            assertThat(dto.getUrl()).isEqualTo("http://url.com");
            assertThat(dto.getDatetime()).isEqualTo(1234567890L);
            assertThat(dto.getSummary()).isEqualTo("Summary text");
            assertThat(dto.getImage()).isEqualTo("http://image.com");
        }

        @Test
        void testGettersAndSetters() {
            MarketNewsDTO dto = new MarketNewsDTO();
            dto.setHeadline("New Headline");
            dto.setSource("New Source");
            dto.setUrl("http://newurl.com");
            dto.setDatetime(9876543210L);
            dto.setSummary("New summary");
            dto.setImage("http://newimage.com");

            assertThat(dto.getHeadline()).isEqualTo("New Headline");
            assertThat(dto.getDatetime()).isEqualTo(9876543210L);
        }

        @Test
        void testWithNullValues() {
            MarketNewsDTO dto = new MarketNewsDTO();
            dto.setDatetime(null);
            dto.setImage(null);

            assertThat(dto.getDatetime()).isNull();
            assertThat(dto.getImage()).isNull();
        }
    }

    @Nested
    @DisplayName("GoalForecastDTO Tests")
    class GoalForecastDTOTests {
        @Test
        void testNoArgsConstructor() {
            GoalForecastDTO dto = new GoalForecastDTO();
            assertThat(dto).isNotNull();
        }

        @Test
        void testAllArgsConstructor() {
            LocalDate targetDate = LocalDate.now().plusYears(5);
            List<GoalForecastDTO.ForecastPoint> trajectory = Arrays.asList(
                    new GoalForecastDTO.ForecastPoint(LocalDate.now(), new BigDecimal("10000"))
            );
            
            GoalForecastDTO dto = new GoalForecastDTO(1L, 2L, new BigDecimal("10000"),
                    new BigDecimal("50000"), targetDate, new BigDecimal("8.0"),
                    new BigDecimal("45000"), new BigDecimal("500"), 60, "Narrative", trajectory);
            
            assertThat(dto.getGoalId()).isEqualTo(1L);
            assertThat(dto.getPortfolioId()).isEqualTo(2L);
            assertThat(dto.getCurrentValue()).isEqualByComparingTo(new BigDecimal("10000"));
            assertThat(dto.getTargetAmount()).isEqualByComparingTo(new BigDecimal("50000"));
            assertThat(dto.getMonthsRemaining()).isEqualTo(60);
            assertThat(dto.getTrajectory()).hasSize(1);
        }

        @Test
        void testGettersAndSetters() {
            GoalForecastDTO dto = new GoalForecastDTO();
            LocalDate date = LocalDate.now().plusYears(3);
            
            dto.setGoalId(1L);
            dto.setPortfolioId(2L);
            dto.setCurrentValue(new BigDecimal("20000"));
            dto.setTargetAmount(new BigDecimal("100000"));
            dto.setTargetDate(date);
            dto.setExpectedAnnualReturn(new BigDecimal("7.5"));
            dto.setProjectedValue(new BigDecimal("90000"));
            dto.setRequiredMonthlyContribution(new BigDecimal("1000"));
            dto.setMonthsRemaining(36);
            dto.setNarrative("Test narrative");
            dto.setTrajectory(Arrays.asList(
                    new GoalForecastDTO.ForecastPoint(date, new BigDecimal("20000"))
            ));

            assertThat(dto.getGoalId()).isEqualTo(1L);
            assertThat(dto.getCurrentValue()).isEqualByComparingTo(new BigDecimal("20000"));
            assertThat(dto.getMonthsRemaining()).isEqualTo(36);
        }

        @Nested
        @DisplayName("ForecastPoint Tests")
        class ForecastPointTests {
            @Test
            void testNoArgsConstructor() {
                GoalForecastDTO.ForecastPoint point = new GoalForecastDTO.ForecastPoint();
                assertThat(point).isNotNull();
            }

            @Test
            void testAllArgsConstructor() {
                LocalDate date = LocalDate.now();
                GoalForecastDTO.ForecastPoint point = new GoalForecastDTO.ForecastPoint(date, new BigDecimal("10000"));
                
                assertThat(point.getDate()).isEqualTo(date);
                assertThat(point.getValue()).isEqualByComparingTo(new BigDecimal("10000"));
            }

            @Test
            void testGettersAndSetters() {
                GoalForecastDTO.ForecastPoint point = new GoalForecastDTO.ForecastPoint();
                LocalDate date = LocalDate.now();
                
                point.setDate(date);
                point.setValue(new BigDecimal("15000"));

                assertThat(point.getDate()).isEqualTo(date);
                assertThat(point.getValue()).isEqualByComparingTo(new BigDecimal("15000"));
            }
        }
    }

    @Nested
    @DisplayName("WhatIfRequestDTO Tests")
    class WhatIfRequestDTOTests {
        @Test
        void testNoArgsConstructor() {
            WhatIfRequestDTO dto = new WhatIfRequestDTO();
            assertThat(dto).isNotNull();
        }

        @Test
        void testAllArgsConstructor() {
            LocalDate date = LocalDate.now().plusYears(2);
            WhatIfRequestDTO dto = new WhatIfRequestDTO(new BigDecimal("500"), 
                    new BigDecimal("8.0"), 24, date);
            
            assertThat(dto.getMonthlyContribution()).isEqualByComparingTo(new BigDecimal("500"));
            assertThat(dto.getExpectedAnnualReturn()).isEqualByComparingTo(new BigDecimal("8.0"));
            assertThat(dto.getMonths()).isEqualTo(24);
            assertThat(dto.getTargetDate()).isEqualTo(date);
        }

        @Test
        void testGettersAndSetters() {
            WhatIfRequestDTO dto = new WhatIfRequestDTO();
            LocalDate date = LocalDate.now().plusMonths(12);
            
            dto.setMonthlyContribution(new BigDecimal("1000"));
            dto.setExpectedAnnualReturn(new BigDecimal("7.5"));
            dto.setMonths(12);
            dto.setTargetDate(date);

            assertThat(dto.getMonthlyContribution()).isEqualByComparingTo(new BigDecimal("1000"));
            assertThat(dto.getMonths()).isEqualTo(12);
        }

        @Test
        void testWithNullValues() {
            WhatIfRequestDTO dto = new WhatIfRequestDTO();
            dto.setMonths(null);
            dto.setTargetDate(null);

            assertThat(dto.getMonths()).isNull();
            assertThat(dto.getTargetDate()).isNull();
        }
    }

    @Nested
    @DisplayName("WhatIfResponseDTO Tests")
    class WhatIfResponseDTOTests {
        @Test
        void testNoArgsConstructor() {
            WhatIfResponseDTO dto = new WhatIfResponseDTO();
            assertThat(dto).isNotNull();
        }

        @Test
        void testAllArgsConstructor() {
            LocalDate endDate = LocalDate.now().plusMonths(24);
            List<GoalForecastDTO.ForecastPoint> trajectory = Arrays.asList(
                    new GoalForecastDTO.ForecastPoint(LocalDate.now(), new BigDecimal("10000"))
            );
            
            WhatIfResponseDTO dto = new WhatIfResponseDTO(
                    new BigDecimal("10000"),
                    new BigDecimal("20000"),
                    24,
                    new BigDecimal("8.0"),
                    new BigDecimal("500"),
                    trajectory,
                    "Narrative",
                    endDate
            );
            
            assertThat(dto.getCurrentValue()).isEqualByComparingTo(new BigDecimal("10000"));
            assertThat(dto.getProjectedValue()).isEqualByComparingTo(new BigDecimal("20000"));
            assertThat(dto.getMonths()).isEqualTo(24);
            assertThat(dto.getEndDate()).isEqualTo(endDate);
        }

        @Test
        void testGettersAndSetters() {
            WhatIfResponseDTO dto = new WhatIfResponseDTO();
            LocalDate endDate = LocalDate.now().plusMonths(12);
            
            dto.setCurrentValue(new BigDecimal("15000"));
            dto.setProjectedValue(new BigDecimal("25000"));
            dto.setMonths(12);
            dto.setExpectedAnnualReturn(new BigDecimal("7.5"));
            dto.setMonthlyContribution(new BigDecimal("1000"));
            dto.setNarrative("Test narrative");
            dto.setEndDate(endDate);

            assertThat(dto.getCurrentValue()).isEqualByComparingTo(new BigDecimal("15000"));
            assertThat(dto.getMonths()).isEqualTo(12);
        }

        @Test
        void testWithNullTrajectory() {
            WhatIfResponseDTO dto = new WhatIfResponseDTO();
            dto.setTrajectory(null);

            assertThat(dto.getTrajectory()).isNull();
        }

        @Test
        void testWithEmptyTrajectory() {
            WhatIfResponseDTO dto = new WhatIfResponseDTO();
            dto.setTrajectory(new ArrayList<>());

            assertThat(dto.getTrajectory()).isEmpty();
        }
    }

    @Nested
    @DisplayName("PortfolioDashboardDTO Tests")
    class PortfolioDashboardDTOTests {
        @Test
        void testNoArgsConstructor() {
            PortfolioDashboardDTO dto = new PortfolioDashboardDTO();
            assertThat(dto).isNotNull();
        }

        @Test
        void testAllArgsConstructor() {
            LocalDateTime now = LocalDateTime.now();
            List<PortfolioDashboardDTO.HoldingDetailDTO> holdings = Arrays.asList(
                    new PortfolioDashboardDTO.HoldingDetailDTO()
            );
            
            PortfolioDashboardDTO dto = new PortfolioDashboardDTO(1L, "My Portfolio", 
                    "USD", new BigDecimal("100000"), holdings, now);
            
            assertThat(dto.getPortfolioId()).isEqualTo(1L);
            assertThat(dto.getPortfolioName()).isEqualTo("My Portfolio");
            assertThat(dto.getTotalValue()).isEqualByComparingTo(new BigDecimal("100000"));
        }

        @Test
        void testGettersAndSetters() {
            PortfolioDashboardDTO dto = new PortfolioDashboardDTO();
            LocalDateTime now = LocalDateTime.now();
            
            dto.setPortfolioId(1L);
            dto.setPortfolioName("Test Portfolio");
            dto.setBaseCurrency("EUR");
            dto.setTotalValue(new BigDecimal("150000"));
            dto.setCreatedAt(now);

            assertThat(dto.getPortfolioId()).isEqualTo(1L);
            assertThat(dto.getTotalValue()).isEqualByComparingTo(new BigDecimal("150000"));
        }

        @Nested
        @DisplayName("HoldingDetailDTO Tests")
        class HoldingDetailDTOTests {
            @Test
            void testNoArgsConstructor() {
                PortfolioDashboardDTO.HoldingDetailDTO dto = new PortfolioDashboardDTO.HoldingDetailDTO();
                assertThat(dto).isNotNull();
            }

            @Test
            void testAllArgsConstructor() {
                LocalDate date = LocalDate.now();
                PortfolioDashboardDTO.HoldingDetailDTO dto = new PortfolioDashboardDTO.HoldingDetailDTO(
                        1L, "Apple", "STOCK", new BigDecimal("10"), 
                        new BigDecimal("150"), new BigDecimal("175"), "USD", date);
                
                assertThat(dto.getHoldingId()).isEqualTo(1L);
                assertThat(dto.getAssetName()).isEqualTo("Apple");
                assertThat(dto.getAssetType()).isEqualTo("STOCK");
                assertThat(dto.getQuantity()).isEqualByComparingTo(new BigDecimal("10"));
                assertThat(dto.getPurchasePrice()).isEqualByComparingTo(new BigDecimal("150"));
                assertThat(dto.getCurrentPrice()).isEqualByComparingTo(new BigDecimal("175"));
                assertThat(dto.getCurrency()).isEqualTo("USD");
                assertThat(dto.getPurchaseDate()).isEqualTo(date);
                // Calculated fields
                assertThat(dto.getTotalInvested()).isEqualByComparingTo(new BigDecimal("1500"));
                assertThat(dto.getCurrentValue()).isEqualByComparingTo(new BigDecimal("1750"));
                assertThat(dto.getGainLoss()).isEqualByComparingTo(new BigDecimal("250"));
            }

            @Test
            void testGettersAndSetters() {
                PortfolioDashboardDTO.HoldingDetailDTO dto = new PortfolioDashboardDTO.HoldingDetailDTO();
                LocalDate date = LocalDate.now();
                
                dto.setHoldingId(1L);
                dto.setAssetName("Apple");
                dto.setAssetType("STOCK");
                dto.setQuantity(new BigDecimal("10"));
                dto.setPurchasePrice(new BigDecimal("150"));
                dto.setCurrentPrice(new BigDecimal("175"));
                dto.setCurrency("USD");
                dto.setPurchaseDate(date);
                dto.setTotalInvested(new BigDecimal("1500"));
                dto.setCurrentValue(new BigDecimal("1750"));
                dto.setGainLoss(new BigDecimal("250"));
                dto.setGainLossPercentage(new BigDecimal("16.67"));
                dto.setAllocation(new BigDecimal("17.5"));
                dto.setTargetValue(new BigDecimal("2000"));
                dto.setValueDrift(new BigDecimal("250"));
                dto.setValueDriftPercentage(new BigDecimal("12.5"));

                assertThat(dto.getHoldingId()).isEqualTo(1L);
                assertThat(dto.getAssetName()).isEqualTo("Apple");
                assertThat(dto.getCurrentValue()).isEqualByComparingTo(new BigDecimal("1750"));
                assertThat(dto.getValueDrift()).isEqualByComparingTo(new BigDecimal("250"));
                assertThat(dto.getGainLossPercentage()).isEqualByComparingTo(new BigDecimal("16.67"));
            }

            @Test
            void testWithNullValues() {
                PortfolioDashboardDTO.HoldingDetailDTO dto = new PortfolioDashboardDTO.HoldingDetailDTO();
                dto.setTargetValue(null);
                dto.setValueDrift(null);
                dto.setValueDriftPercentage(null);
                
                assertThat(dto.getTargetValue()).isNull();
                assertThat(dto.getValueDrift()).isNull();
                assertThat(dto.getValueDriftPercentage()).isNull();
            }

            @Test
            void testZeroQuantityCalculation() {
                LocalDate date = LocalDate.now();
                PortfolioDashboardDTO.HoldingDetailDTO dto = new PortfolioDashboardDTO.HoldingDetailDTO(
                        1L, "Test", "STOCK", BigDecimal.ZERO, 
                        new BigDecimal("100"), new BigDecimal("110"), "USD", date);
                
                assertThat(dto.getTotalInvested()).isEqualByComparingTo(BigDecimal.ZERO);
                assertThat(dto.getCurrentValue()).isEqualByComparingTo(BigDecimal.ZERO);
                assertThat(dto.getGainLoss()).isEqualByComparingTo(BigDecimal.ZERO);
                assertThat(dto.getGainLossPercentage()).isEqualByComparingTo(BigDecimal.ZERO);
            }
        }
    }

    @Nested
    @DisplayName("PortfolioDriftDTO Tests")
    class PortfolioDriftDTOTests {
        @Test
        void testNoArgsConstructor() {
            PortfolioDriftDTO dto = new PortfolioDriftDTO();
            assertThat(dto).isNotNull();
        }

        @Test
        void testAllArgsConstructor() {
            LocalDate initialDate = LocalDate.now().minusMonths(6);
            LocalDate latestDate = LocalDate.now();
            List<PortfolioDriftDTO.TimelineEntry> timeline = Arrays.asList(
                    new PortfolioDriftDTO.TimelineEntry()
            );
            
            PortfolioDriftDTO dto = new PortfolioDriftDTO(1L, "My Portfolio", "USD",
                    new BigDecimal("100000"), new BigDecimal("120000"),
                    new BigDecimal("20000"), new BigDecimal("20.0"),
                    initialDate, latestDate, "Narrative", timeline);
            
            assertThat(dto.getPortfolioId()).isEqualTo(1L);
            assertThat(dto.getInitialValue()).isEqualByComparingTo(new BigDecimal("100000"));
            assertThat(dto.getLatestValue()).isEqualByComparingTo(new BigDecimal("120000"));
            assertThat(dto.getDriftPercent()).isEqualByComparingTo(new BigDecimal("20.0"));
        }

        @Test
        void testGettersAndSetters() {
            PortfolioDriftDTO dto = new PortfolioDriftDTO();
            LocalDate initialDate = LocalDate.now().minusMonths(12);
            LocalDate latestDate = LocalDate.now();
            
            dto.setPortfolioId(1L);
            dto.setPortfolioName("Test Portfolio");
            dto.setBaseCurrency("EUR");
            dto.setInitialValue(new BigDecimal("50000"));
            dto.setLatestValue(new BigDecimal("60000"));
            dto.setDriftValue(new BigDecimal("10000"));
            dto.setDriftPercent(new BigDecimal("20.0"));
            dto.setInitialDate(initialDate);
            dto.setLatestDate(latestDate);
            dto.setNarrative("Test narrative");

            assertThat(dto.getPortfolioId()).isEqualTo(1L);
            assertThat(dto.getDriftValue()).isEqualByComparingTo(new BigDecimal("10000"));
        }

        @Nested
        @DisplayName("TimelineEntry Tests")
        class TimelineEntryTests {
            @Test
            void testNoArgsConstructor() {
                PortfolioDriftDTO.TimelineEntry entry = new PortfolioDriftDTO.TimelineEntry();
                assertThat(entry).isNotNull();
            }

            @Test
            void testAllArgsConstructor() {
                LocalDate date = LocalDate.now();
                PortfolioDriftDTO.TimelineEntry entry = new PortfolioDriftDTO.TimelineEntry(
                        date, new BigDecimal("110000"), new BigDecimal("10000"), 
                        new BigDecimal("10.0"), "Test story");
                
                assertThat(entry.getDate()).isEqualTo(date);
                assertThat(entry.getTotalValue()).isEqualByComparingTo(new BigDecimal("110000"));
                assertThat(entry.getDriftFromStart()).isEqualByComparingTo(new BigDecimal("10000"));
                assertThat(entry.getDriftPercentFromStart()).isEqualByComparingTo(new BigDecimal("10.0"));
                assertThat(entry.getStory()).isEqualTo("Test story");
            }

            @Test
            void testGettersAndSetters() {
                PortfolioDriftDTO.TimelineEntry entry = new PortfolioDriftDTO.TimelineEntry();
                LocalDate date = LocalDate.now();
                
                entry.setDate(date);
                entry.setTotalValue(new BigDecimal("110000"));
                entry.setDriftFromStart(new BigDecimal("10000"));
                entry.setDriftPercentFromStart(new BigDecimal("10.0"));
                entry.setStory("Test story");

                assertThat(entry.getDate()).isEqualTo(date);
                assertThat(entry.getTotalValue()).isEqualByComparingTo(new BigDecimal("110000"));
                assertThat(entry.getDriftFromStart()).isEqualByComparingTo(new BigDecimal("10000"));
                assertThat(entry.getDriftPercentFromStart()).isEqualByComparingTo(new BigDecimal("10.0"));
                assertThat(entry.getStory()).isEqualTo("Test story");
            }

            @Test
            void testWithNullValues() {
                PortfolioDriftDTO.TimelineEntry entry = new PortfolioDriftDTO.TimelineEntry();
                entry.setStory(null);
                
                assertThat(entry.getStory()).isNull();
            }
        }
    }

    @Nested
    @DisplayName("TrendForecastDTO Tests")
    class TrendForecastDTOTests {
        @Test
        void testNoArgsConstructor() {
            TrendForecastDTO dto = new TrendForecastDTO();
            assertThat(dto).isNotNull();
        }

        @Test
        void testAllArgsConstructor() {
            List<TrendForecastDTO.Point> actual = Arrays.asList(
                    new TrendForecastDTO.Point(LocalDate.now(), new BigDecimal("10000"))
            );
            List<TrendForecastDTO.Point> forecast = Arrays.asList(
                    new TrendForecastDTO.Point(LocalDate.now().plusMonths(1), new BigDecimal("10500"))
            );
            List<TrendForecastDTO.Point> movingAverage = Arrays.asList(
                    new TrendForecastDTO.Point(LocalDate.now(), new BigDecimal("10250"))
            );
            
            TrendForecastDTO dto = new TrendForecastDTO(1L, actual, forecast, movingAverage, "Narrative");
            
            assertThat(dto.getPortfolioId()).isEqualTo(1L);
            assertThat(dto.getActual()).isEqualTo(actual);
            assertThat(dto.getForecast()).isEqualTo(forecast);
            assertThat(dto.getMovingAverage()).isEqualTo(movingAverage);
            assertThat(dto.getNarrative()).isEqualTo("Narrative");
        }

        @Test
        void testGettersAndSetters() {
            TrendForecastDTO dto = new TrendForecastDTO();
            List<TrendForecastDTO.Point> points = Arrays.asList(
                    new TrendForecastDTO.Point(LocalDate.now(), new BigDecimal("10000"))
            );
            
            dto.setPortfolioId(1L);
            dto.setActual(points);
            dto.setForecast(points);
            dto.setMovingAverage(points);
            dto.setNarrative("Test narrative");

            assertThat(dto.getPortfolioId()).isEqualTo(1L);
            assertThat(dto.getActual()).isEqualTo(points);
        }

        @Nested
        @DisplayName("Point Tests")
        class PointTests {
            @Test
            void testNoArgsConstructor() {
                TrendForecastDTO.Point point = new TrendForecastDTO.Point();
                assertThat(point).isNotNull();
            }

            @Test
            void testAllArgsConstructor() {
                LocalDate date = LocalDate.now();
                TrendForecastDTO.Point point = new TrendForecastDTO.Point(date, new BigDecimal("10000"));
                
                assertThat(point.getDate()).isEqualTo(date);
                assertThat(point.getValue()).isEqualByComparingTo(new BigDecimal("10000"));
            }

            @Test
            void testGettersAndSetters() {
                TrendForecastDTO.Point point = new TrendForecastDTO.Point();
                LocalDate date = LocalDate.now();
                
                point.setDate(date);
                point.setValue(new BigDecimal("15000"));

                assertThat(point.getDate()).isEqualTo(date);
                assertThat(point.getValue()).isEqualByComparingTo(new BigDecimal("15000"));
            }
        }
    }
}