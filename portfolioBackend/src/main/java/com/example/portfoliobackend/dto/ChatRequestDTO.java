package com.example.portfoliobackend.dto;

import java.util.List;

public class ChatRequestDTO {
    private String message;
    private Long portfolioId;
    private String model;
    private Double temperature;
    private Double topP;
    private Integer maxLength;
    private Double repetitionPenalty;
    private List<ChatMessageDTO> history;

    public ChatRequestDTO() {
    }

    public ChatRequestDTO(String message, Long portfolioId) {
        this.message = message;
        this.portfolioId = portfolioId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getTopP() {
        return topP;
    }

    public void setTopP(Double topP) {
        this.topP = topP;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Double getRepetitionPenalty() {
        return repetitionPenalty;
    }

    public void setRepetitionPenalty(Double repetitionPenalty) {
        this.repetitionPenalty = repetitionPenalty;
    }

    public List<ChatMessageDTO> getHistory() {
        return history;
    }

    public void setHistory(List<ChatMessageDTO> history) {
        this.history = history;
    }
}
