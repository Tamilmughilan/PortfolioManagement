package com.example.portfoliobackend.service;

import com.example.portfoliobackend.dto.MarketNewsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MarketNewsService Unit Tests")
class MarketNewsServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MarketNewsService marketNewsService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(marketNewsService, "restTemplate", restTemplate);
    }

    @Nested
    @DisplayName("getLatestNews Tests")
    class GetLatestNewsTests {

        @Test
        @DisplayName("Should return Alpha Vantage news when available")
        void getLatestNews_WhenAlphaVantageAvailable_ShouldReturnAlphaNews() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "test-key");
            
            Map<String, Object> alphaResponse = createAlphaVantageResponse();
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(alphaResponse);

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).isNotEmpty();
            assertThat(result.get(0).getHeadline()).isEqualTo("Market Update");
        }

        @Test
        @DisplayName("Should fallback to Finnhub when Alpha Vantage returns empty")
        void getLatestNews_WhenAlphaVantageEmpty_ShouldFallbackToFinnhub() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "test-key");
            ReflectionTestUtils.setField(marketNewsService, "finnhubKey", "finnhub-key");
            
            // Alpha Vantage returns empty
            when(restTemplate.getForObject(anyString(), eq(Map.class)))
                    .thenReturn(new HashMap<>())
                    .thenReturn(createFinnhubResponse());

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).isNotEmpty();
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
        @DisplayName("Should return empty list when Alpha Vantage has rate limit")
        void getLatestNews_WhenAlphaVantageRateLimited_ShouldReturnEmpty() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "test-key");
            
            Map<String, Object> rateLimitResponse = new HashMap<>();
            rateLimitResponse.put("Note", "API call frequency");
            
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(rateLimitResponse);

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty list when Alpha Vantage has information message")
        void getLatestNews_WhenAlphaVantageInformation_ShouldReturnEmpty() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "test-key");
            
            Map<String, Object> infoResponse = new HashMap<>();
            infoResponse.put("Information", "API key invalid");
            
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(infoResponse);

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Alpha Vantage News Tests")
    class AlphaVantageTests {

        @Test
        @DisplayName("Should parse Alpha Vantage news correctly")
        void fetchAlphaVantageNews_WithValidData_ShouldParseCorrectly() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "test-key");
            
            Map<String, Object> response = createAlphaVantageResponse();
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getHeadline()).isEqualTo("Market Update");
            assertThat(result.get(0).getSource()).isEqualTo("Reuters");
            assertThat(result.get(0).getUrl()).isEqualTo("http://example.com/news");
        }

        @Test
        @DisplayName("Should handle null feed in Alpha Vantage response")
        void fetchAlphaVantageNews_WhenFeedNull_ShouldReturnEmpty() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "test-key");
            
            Map<String, Object> response = new HashMap<>();
            response.put("feed", null);
            
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should parse Alpha Vantage time correctly")
        void parseAlphaVantageTime_WithValidFormat_ShouldParse() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "test-key");
            
            Map<String, Object> item = new HashMap<>();
            item.put("title", "Test News");
            item.put("source", "Test Source");
            item.put("url", "http://test.com");
            item.put("summary", "Test summary");
            item.put("banner_image", "http://test.com/image.jpg");
            item.put("time_published", "20240101T120000");
            
            Map<String, Object> response = new HashMap<>();
            response.put("feed", Arrays.asList(item));
            
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getDatetime()).isNotNull();
        }

        @Test
        @DisplayName("Should handle invalid time format in Alpha Vantage")
        void parseAlphaVantageTime_WithInvalidFormat_ShouldReturnNull() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "test-key");
            
            Map<String, Object> item = new HashMap<>();
            item.put("title", "Test News");
            item.put("source", "Test Source");
            item.put("url", "http://test.com");
            item.put("summary", "Test summary");
            item.put("banner_image", "");
            item.put("time_published", "invalid-format");
            
            Map<String, Object> response = new HashMap<>();
            response.put("feed", Arrays.asList(item));
            
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getDatetime()).isNull();
        }

        @Test
        @DisplayName("Should handle null time in Alpha Vantage")
        void parseAlphaVantageTime_WithNullTime_ShouldReturnNull() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "test-key");
            
            Map<String, Object> item = new HashMap<>();
            item.put("title", "Test News");
            item.put("source", "Test Source");
            item.put("url", "http://test.com");
            item.put("summary", "Test summary");
            item.put("banner_image", "");
            item.put("time_published", null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("feed", Arrays.asList(item));
            
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getDatetime()).isNull();
        }

        @Test
        @DisplayName("Should handle exception in Alpha Vantage fetch")
        void fetchAlphaVantageNews_WhenException_ShouldReturnEmpty() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "test-key");
            
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenThrow(new RuntimeException("API Error"));

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Finnhub News Tests")
    class FinnhubTests {

        @Test
        @DisplayName("Should parse Finnhub news correctly")
        void fetchFinnhubNews_WithValidData_ShouldParseCorrectly() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "");
            ReflectionTestUtils.setField(marketNewsService, "finnhubKey", "test-key");
            
            List<Map<String, Object>> response = createFinnhubResponse();
            when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(response);

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getHeadline()).isEqualTo("Finnhub News");
            assertThat(result.get(0).getSource()).isEqualTo("Bloomberg");
        }

        @Test
        @DisplayName("Should handle null response from Finnhub")
        void fetchFinnhubNews_WhenResponseNull_ShouldReturnEmpty() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "");
            ReflectionTestUtils.setField(marketNewsService, "finnhubKey", "test-key");
            
            when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(null);

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should parse datetime from Number in Finnhub")
        void fetchFinnhubNews_WithNumberDatetime_ShouldParse() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "");
            ReflectionTestUtils.setField(marketNewsService, "finnhubKey", "test-key");
            
            Map<String, Object> item = new HashMap<>();
            item.put("headline", "Test");
            item.put("source", "Test");
            item.put("url", "http://test.com");
            item.put("summary", "Summary");
            item.put("image", "");
            item.put("datetime", 1234567890L);
            
            when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(Arrays.asList(item));

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getDatetime()).isEqualTo(1234567890L);
        }

        @Test
        @DisplayName("Should handle non-Number datetime in Finnhub")
        void fetchFinnhubNews_WithNonNumberDatetime_ShouldSetNull() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "");
            ReflectionTestUtils.setField(marketNewsService, "finnhubKey", "test-key");
            
            Map<String, Object> item = new HashMap<>();
            item.put("headline", "Test");
            item.put("source", "Test");
            item.put("url", "http://test.com");
            item.put("summary", "Summary");
            item.put("image", "");
            item.put("datetime", "invalid");
            
            when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(Arrays.asList(item));

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getDatetime()).isNull();
        }

        @Test
        @DisplayName("Should handle exception in Finnhub fetch")
        void fetchFinnhubNews_WhenException_ShouldReturnEmpty() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "");
            ReflectionTestUtils.setField(marketNewsService, "finnhubKey", "test-key");
            
            when(restTemplate.getForObject(anyString(), eq(List.class))).thenThrow(new RuntimeException("API Error"));

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when Finnhub key is missing")
        void fetchFinnhubNews_WhenKeyMissing_ShouldReturnEmpty() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "");
            ReflectionTestUtils.setField(marketNewsService, "finnhubKey", "");

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle multiple news items")
        void getLatestNews_WithMultipleItems_ShouldReturnAll() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "test-key");
            
            Map<String, Object> item1 = new HashMap<>();
            item1.put("title", "News 1");
            item1.put("source", "Source 1");
            item1.put("url", "http://1.com");
            item1.put("summary", "Summary 1");
            item1.put("banner_image", "");
            item1.put("time_published", "20240101T120000");
            
            Map<String, Object> item2 = new HashMap<>();
            item2.put("title", "News 2");
            item2.put("source", "Source 2");
            item2.put("url", "http://2.com");
            item2.put("summary", "Summary 2");
            item2.put("banner_image", "");
            item2.put("time_published", "20240101T130000");
            
            Map<String, Object> response = new HashMap<>();
            response.put("feed", Arrays.asList(item1, item2));
            
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Should handle missing fields in news item")
        void getLatestNews_WithMissingFields_ShouldUseDefaults() {
            ReflectionTestUtils.setField(marketNewsService, "apiKey", "test-key");
            
            Map<String, Object> item = new HashMap<>();
            item.put("title", "News");
            // Missing other fields
            
            Map<String, Object> response = new HashMap<>();
            response.put("feed", Arrays.asList(item));
            
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);

            List<MarketNewsDTO> result = marketNewsService.getLatestNews();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSource()).isEqualTo("null");
            assertThat(result.get(0).getUrl()).isEqualTo("null");
        }
    }

    private Map<String, Object> createAlphaVantageResponse() {
        Map<String, Object> item = new HashMap<>();
        item.put("title", "Market Update");
        item.put("source", "Reuters");
        item.put("url", "http://example.com/news");
        item.put("summary", "Market summary");
        item.put("banner_image", "http://example.com/image.jpg");
        item.put("time_published", "20240101T120000");
        
        Map<String, Object> response = new HashMap<>();
        response.put("feed", Arrays.asList(item));
        
        return response;
    }

    private List<Map<String, Object>> createFinnhubResponse() {
        Map<String, Object> item = new HashMap<>();
        item.put("headline", "Finnhub News");
        item.put("source", "Bloomberg");
        item.put("url", "http://finnhub.com/news");
        item.put("summary", "Finnhub summary");
        item.put("image", "http://finnhub.com/image.jpg");
        item.put("datetime", 1234567890L);
        
        return Arrays.asList(item);
    }
}
