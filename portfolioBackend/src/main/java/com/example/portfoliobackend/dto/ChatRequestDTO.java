package com.example.portfoliobackend.dto;

public class ChatRequestDTO {
    private String message;
    private Long portfolioId;

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
}
