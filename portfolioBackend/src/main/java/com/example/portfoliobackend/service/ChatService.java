package com.example.portfoliobackend.service;

import com.example.portfoliobackend.dto.ChatRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private static final String REPLICATE_URL = "https://api.replicate.com/v1/predictions";
    private static final String REPLICATE_MODEL_URL = "https://api.replicate.com/v1/models/%s/predictions";
    private static final int MAX_POLL_ATTEMPTS = 20;
    private static final long POLL_DELAY_MS = 1000;

    @Autowired
    private PortfolioService portfolioService;

    public String chat(ChatRequestDTO request) {
        String token = getEnvValue("REPLICATE_API_TOKEN");
        String version = getEnvValue("REPLICATE_MODEL_VERSION");
        String model = getEnvValue("REPLICATE_MODEL");
        if (token == null || token.isBlank()) {
            return "Replicate API token is missing. Add REPLICATE_API_TOKEN to your .env file.";
        }
        if ((version == null || version.isBlank()) && (model == null || model.isBlank())) {
            return "Replicate model is missing. Add REPLICATE_MODEL (e.g., meta/llama-2-7b-chat) or REPLICATE_MODEL_VERSION to your .env file.";
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
        if (version != null && !version.isBlank()) {
            payload.put("version", version);
        }
        Map<String, Object> input = new HashMap<>();
        input.put("prompt", prompt);
        input.put("max_new_tokens", 256);
        input.put("temperature", 0.6);
        payload.put("input", input);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Token " + token);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        RestTemplate restTemplate = new RestTemplate();
        String url = (version != null && !version.isBlank())
                ? REPLICATE_URL
                : String.format(REPLICATE_MODEL_URL, model);
        try {
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            return pollReplicateResponse(restTemplate, headers, response);
        } catch (Exception ex) {
            return "Chat service error. Please verify your Replicate token and model version.";
        }
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
    private String pollReplicateResponse(RestTemplate restTemplate, HttpHeaders headers, Map<String, Object> response) {
        if (response == null || response.get("id") == null) {
            return "No response from Replicate. Please try again later.";
        }
        String id = response.get("id").toString();
        String status = response.get("status") == null ? "" : response.get("status").toString();
        if ("succeeded".equals(status)) {
            return extractOutput(response);
        }

        for (int i = 0; i < MAX_POLL_ATTEMPTS; i++) {
            try {
                ResponseEntity<Map> result = restTemplate.exchange(
                        REPLICATE_URL + "/" + id,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        Map.class
                );
                Map<String, Object> body = result.getBody();
                if (body == null) {
                    continue;
                }
                String currentStatus = body.get("status") == null ? "" : body.get("status").toString();
                if ("succeeded".equals(currentStatus)) {
                    return extractOutput(body);
                }
                if ("failed".equals(currentStatus) || "canceled".equals(currentStatus)) {
                    return "Replicate could not generate a response. Please try again.";
                }
                Thread.sleep(POLL_DELAY_MS);
            } catch (Exception ignored) {
                return "Replicate did not respond. Please try again later.";
            }
        }
        return "Replicate is taking too long to respond. Please try again.";
    }

    @SuppressWarnings("unchecked")
    private String extractOutput(Map<String, Object> response) {
        Object output = response.get("output");
        if (output instanceof List) {
            return String.join("", ((List<Object>) output).stream().map(Object::toString).toList());
        }
        return output == null ? "No response from Replicate." : output.toString();
    }

    private String getEnvValue(String key) {
        String value = System.getenv(key);
        if (value != null && !value.isBlank()) {
            return value;
        }
        Map<String, String> envFile = loadEnvFile();
        return envFile.get(key);
    }

    private Map<String, String> loadEnvFile() {
        Map<String, String> values = new HashMap<>();
        try {
            Path cwd = Paths.get(System.getProperty("user.dir"));
            Path envPath = cwd.resolve(".env");
            if (!Files.exists(envPath) && cwd.getParent() != null) {
                envPath = cwd.getParent().resolve(".env");
            }
            if (!Files.exists(envPath)) {
                return values;
            }
            List<String> lines = Files.readAllLines(envPath);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#") || !trimmed.contains("=")) {
                    continue;
                }
                String[] parts = trimmed.split("=", 2);
                String key = parts[0].trim();
                String val = parts[1].trim();
                if (val.startsWith("\"") && val.endsWith("\"")) {
                    val = val.substring(1, val.length() - 1);
                }
                values.put(key, val);
            }
        } catch (Exception ignored) {
            return values;
        }
        return values;
    }
}