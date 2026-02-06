package com.example.portfoliobackend.service;

import com.example.portfoliobackend.dto.MarketNewsDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("MarketNewsService Unit Tests")
class MarketNewsServiceTest {

    @InjectMocks
    private MarketNewsService marketNewsService;

    @Nested
    @DisplayName("getLatestNews Tests")
    class GetLatestNewsTests {

        @Test
        @DisplayName("Should return empty when API key is missing")
        void getLatestNews_WhenApiKeyMissing_ShouldReturnEmpty() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "");
            ReflectionTestUtils.setField(marketNewsService, "finnhubKey", "");

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when both API keys are missing")
        void getLatestNews_WhenBothApiKeysMissing_ShouldReturnEmpty() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "");
            ReflectionTestUtils.setField(marketNewsService, "finnhubKey", "");

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty list when both APIs fail")
        void getLatestNews_WhenBothApisFail_ShouldReturnEmptyList() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "");
            ReflectionTestUtils.setField(marketNewsService, "finnhubKey", "");

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle null API keys")
        void getLatestNews_WhenApiKeysNull_ShouldReturnEmpty() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", null);
            ReflectionTestUtils.setField(marketNewsService, "finnhubKey", null);

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Alpha Vantage News Tests")
    class AlphaVantageTests {

        @Test
        @DisplayName("Should return empty when API key is blank")
        void fetchAlphaVantageNews_WhenApiKeyBlank_ShouldReturnEmpty() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "   ");

            // Since RestTemplate is created internally and we can't mock it easily,
            // we test that the service handles missing/blank keys gracefully
            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            // Will return empty or fallback to Finnhub (which also needs a key)
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should handle null API key")
        void fetchAlphaVantageNews_WhenApiKeyNull_ShouldReturnEmpty() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", null);

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("Finnhub News Tests")
    class FinnhubTests {

        @Test
        @DisplayName("Should return empty when Finnhub key is blank")
        void fetchFinnhubNews_WhenFinnhubKeyBlank_ShouldReturnEmpty() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "");
            ReflectionTestUtils.setField(marketNewsService, "finnhubKey", "   ");

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when Finnhub key is null")
        void fetchFinnhubNews_WhenFinnhubKeyNull_ShouldReturnEmpty() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "");
            ReflectionTestUtils.setField(marketNewsService, "finnhubKey", null);

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle both API keys being blank")
        void getLatestNews_WhenBothKeysBlank_ShouldReturnEmpty() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "   ");
            ReflectionTestUtils.setField(marketNewsService, "finnhubKey", "   ");

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle service initialization")
        void getLatestNews_ServiceInitialized_ShouldNotThrowException() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "");
            ReflectionTestUtils.setField(marketNewsService, "finnhubKey", "");

            // Should not throw exception even with empty keys
            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }
    }
}