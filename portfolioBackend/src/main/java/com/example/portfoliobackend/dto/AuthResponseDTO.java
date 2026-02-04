package com.example.portfoliobackend.dto;

import java.time.LocalDateTime;

public class AuthResponseDTO {
    private Long userId;
    private String username;
    private String email;
    private String defaultCurrency;
    private LocalDateTime createdAt;

    public AuthResponseDTO() {
    }

    public AuthResponseDTO(Long userId, String username, String email, String defaultCurrency, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.defaultCurrency = defaultCurrency;
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
