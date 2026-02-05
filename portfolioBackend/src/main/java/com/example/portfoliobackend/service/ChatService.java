package com.example.portfoliobackend.service;

import com.example.portfoliobackend.dto.ChatRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent";

    @Value("${gemini.api.key:}")
    private String geminiKey;

    @Autowired
    private PortfolioService portfolioService;

    public String chat(ChatRequestDTO request) {
        if (geminiKey == null || geminiKey.isBlank()) {
            return "Gemini API key is missing. Please add gemini.api.key in application.properties.";
        }
        String userMessage = request.getMessage() == null ? "" : request.getMessage().trim();
        if (userMessage.isEmpty()) {
            return "Ask me anything about stocks, markets, or your portfolio.";
        }

        String context = buildContext(request.getPortfolioId());
        String prompt = "You are a friendly financial education assistant. "
                + "Provide general market education, concepts, and high-level guidance. "
                + "Do NOT provide personalized financial advice or specific buy/sell instructions. "
                + "If asked for recommendations, include a brief disclaimer and suggest consulting a qualified advisor. "
                + "Keep answers concise, practical, and user-friendly. "
                + context
                + "\nUser question: " + userMessage;

        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);
        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(textPart));
        payload.put("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", geminiKey);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.postForObject(GEMINI_URL, entity, Map.class);
        return extractReply(response);
    }

    private String buildContext(Long portfolioId) {
        if (portfolioId == null) {
            return "";
        }
        BigDecimal totalValue = portfolioService.calculateTotalValue(portfolioId);
        int holdingsCount = portfolioService.getHoldingsByPortfolioId(portfolioId).size();
        return "\nPortfolio context: total value = " + totalValue + ", holdings count = " + holdingsCount + ".";
    }

    @SuppressWarnings("unchecked")
    private String extractReply(Map<String, Object> response) {
        if (response == null || response.get("candidates") == null) {
            return "No response from Gemini. Please try again later.";
        }
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates.isEmpty()) {
                return "No response from Gemini. Please try again later.";
            }
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts == null || parts.isEmpty()) {
                return "No response from Gemini. Please try again later.";
            }
            Object text = parts.get(0).get("text");
            return text == null ? "No response from Gemini. Please try again later." : text.toString();
        } catch (Exception ignored) {
            return "No response from Gemini. Please try again later.";
        }
    }
}