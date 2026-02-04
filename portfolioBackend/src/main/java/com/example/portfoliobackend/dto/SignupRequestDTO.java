package com.example.portfoliobackend.dto;

public class SignupRequestDTO {
    private String username;
    private String email;
    private String password;
    private String defaultCurrency;

    public SignupRequestDTO() {
    }

    public SignupRequestDTO(String username, String email, String password, String defaultCurrency) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.defaultCurrency = defaultCurrency;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }
}
