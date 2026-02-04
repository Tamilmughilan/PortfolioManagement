package com.example.portfoliobackend.service;

import com.example.portfoliobackend.dto.MarketNewsDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MarketNewsService {

    private static final String ALPHA_VANTAGE_URL =
            "https://www.alphavantage.co/query?function=NEWS_SENTIMENT&apikey=%s";
    private static final String FINNHUB_URL =
            "https://finnhub.io/api/v1/news?category=general&token=%s";
    private static final DateTimeFormatter ALPHA_TIME =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    @Value("${alphavantage.api.key:}")
    private String apiKey;

    @Value("${finnhub.api.key:}")
    private String finnhubKey;

    public List<MarketNewsDTO> getLatestNews() {
        List<MarketNewsDTO> alpha = fetchAlphaVantageNews();
        if (!alpha.isEmpty()) {
            return alpha;
        }
        return fetchFinnhubNews();
    }

    private Long parseAlphaVantageTime(String timePublished) {
        if (timePublished == null || timePublished.isBlank()) {
            return null;
        }
        try {
            LocalDateTime dateTime = LocalDateTime.parse(timePublished, ALPHA_TIME);
            return dateTime.toEpochSecond(ZoneOffset.UTC);
        } catch (Exception ignored) {
            return null;
        }
    }

    private List<MarketNewsDTO> fetchAlphaVantageNews() {
        if (apiKey == null || apiKey.isBlank()) {
            return new ArrayList<>();
        }
        try {
            String url = String.format(ALPHA_VANTAGE_URL, apiKey);
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null || response.get("feed") == null) {
                return new ArrayList<>();
            }
            if (response.containsKey("Note") || response.containsKey("Information")) {
                return new ArrayList<>();
            }
            List<Map<String, Object>> feed = (List<Map<String, Object>>) response.get("feed");
            List<MarketNewsDTO> results = new ArrayList<>();
            for (Map<String, Object> item : feed) {
                String headline = String.valueOf(item.getOrDefault("title", ""));
                String source = String.valueOf(item.getOrDefault("source", ""));
                String newsUrl = String.valueOf(item.getOrDefault("url", ""));
                String summary = String.valueOf(item.getOrDefault("summary", ""));
                String image = String.valueOf(item.getOrDefault("banner_image", ""));
                Long datetime = parseAlphaVantageTime(String.valueOf(item.getOrDefault("time_published", "")));
                results.add(new MarketNewsDTO(headline, source, newsUrl, datetime, summary, image));
            }
            return results;
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }

    private List<MarketNewsDTO> fetchFinnhubNews() {
        if (finnhubKey == null || finnhubKey.isBlank()) {
            return new ArrayList<>();
        }
        try {
            String url = String.format(FINNHUB_URL, finnhubKey);
            RestTemplate restTemplate = new RestTemplate();
            List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);
            if (response == null) {
                return new ArrayList<>();
            }
            List<MarketNewsDTO> results = new ArrayList<>();
            for (Map<String, Object> item : response) {
                String headline = String.valueOf(item.getOrDefault("headline", ""));
                String source = String.valueOf(item.getOrDefault("source", ""));
                String newsUrl = String.valueOf(item.getOrDefault("url", ""));
                String summary = String.valueOf(item.getOrDefault("summary", ""));
                String image = String.valueOf(item.getOrDefault("image", ""));
                Long datetime = null;
                Object timeValue = item.get("datetime");
                if (timeValue instanceof Number) {
                    datetime = ((Number) timeValue).longValue();
                }
                results.add(new MarketNewsDTO(headline, source, newsUrl, datetime, summary, image));
            }
            return results;
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }
}
