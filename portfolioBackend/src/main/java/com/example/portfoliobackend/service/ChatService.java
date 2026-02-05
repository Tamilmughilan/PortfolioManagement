package com.example.portfoliobackend.service;

import com.example.portfoliobackend.dto.ChatMessageDTO;
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
    private static final String DEFAULT_MODEL = "a16z-infra/llama7b-v2-chat";
    private static final double DEFAULT_TEMPERATURE = 0.6;
    private static final double DEFAULT_TOP_P = 0.9;
    private static final int DEFAULT_MAX_LENGTH = 180;
    private static final double DEFAULT_REPETITION_PENALTY = 1.0;

    @Autowired
    private PortfolioService portfolioService;

    public String chat(ChatRequestDTO request) {
        String token = getEnvValue("REPLICATE_API_TOKEN");
        String version = getEnvValue("REPLICATE_MODEL_VERSION");
        String model = getEnvValue("REPLICATE_MODEL");
        String effectiveModel = (model != null && !model.isBlank()) ? model : DEFAULT_MODEL;
        String effectiveVersion = version;
        if (token == null || token.isBlank()) {
            return "Replicate API token is missing. Add REPLICATE_API_TOKEN to your .env file.";
        }
        String userMessage = request.getMessage() == null ? "" : request.getMessage().trim();
        if (userMessage.isEmpty()) {
            return "Ask me anything about stocks, markets, or your portfolio.";
        }

        String context = buildContext(request.getPortfolioId());
        String prompt = buildConversationPrompt(request.getHistory(), userMessage, context);

        Map<String, Object> payload = new HashMap<>();
        if (effectiveVersion != null && !effectiveVersion.isBlank()) {
            payload.put("version", effectiveVersion);
        }
        Map<String, Object> input = new HashMap<>();
        input.put("prompt", prompt);
        // Constant settings for simplicity
        input.put("temperature", DEFAULT_TEMPERATURE);
        input.put("top_p", DEFAULT_TOP_P);
        input.put("max_length", DEFAULT_MAX_LENGTH);
        input.put("repetition_penalty", DEFAULT_REPETITION_PENALTY);
        payload.put("input", input);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Token " + token);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        RestTemplate restTemplate = new RestTemplate();
        String url = (effectiveVersion != null && !effectiveVersion.isBlank())
                ? REPLICATE_URL
                : String.format(REPLICATE_MODEL_URL, effectiveModel);
        try {
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            return pollReplicateResponse(restTemplate, headers, response);
        } catch (Exception ex) {
            return "Chat service error. Please verify your Replicate token and model version.";
        }
    }

    private String buildConversationPrompt(List<ChatMessageDTO> history, String userMessage, String context) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a friendly financial education assistant.\n")
                .append("Provide general market education, concepts, and high-level guidance.\n")
                .append("Do NOT provide personalized financial advice or specific buy/sell instructions.\n")
                .append("If asked for recommendations, include a brief disclaimer and suggest consulting a qualified advisor.\n")
                .append("Keep answers concise, practical, and user-friendly.\n");
        if (context != null && !context.isBlank()) {
            sb.append(context).append("\n");
        }
        sb.append("\nConversation:\n");
        if (history != null) {
            for (ChatMessageDTO msg : history) {
                if (msg == null || msg.getContent() == null) continue;
                String role = msg.getRole() == null ? "" : msg.getRole().trim().toLowerCase();
                if ("user".equals(role)) {
                    sb.append("User: ").append(msg.getContent()).append("\n");
                } else if ("assistant".equals(role) || "model".equals(role)) {
                    sb.append("Assistant: ").append(msg.getContent()).append("\n");
                }
            }
        }
        sb.append("User: ").append(userMessage).append("\nAssistant:");
        return sb.toString();
    }

    private double clampDouble(Double value, double min, double max, double def) {
        if (value == null) return def;
        double v = value;
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    private int clampInt(Integer value, int min, int max, int def) {
        if (value == null) return def;
        int v = value;
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    private static class ParsedModel {
        String model;
        String version;

        ParsedModel(String model, String version) {
            this.model = model;
            this.version = version;
        }
    }

    private ParsedModel parseModel(String modelOrModelAndVersion) {
        if (modelOrModelAndVersion == null) return new ParsedModel(null, null);
        String trimmed = modelOrModelAndVersion.trim();
        if (trimmed.isEmpty()) return new ParsedModel(null, null);
        if (trimmed.contains(":")) {
            String[] parts = trimmed.split(":", 2);
            String m = parts[0].trim();
            String v = parts[1].trim();
            if (m.isEmpty()) m = null;
            if (v.isEmpty()) v = null;
            return new ParsedModel(m, v);
        }
        return new ParsedModel(trimmed, null);
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