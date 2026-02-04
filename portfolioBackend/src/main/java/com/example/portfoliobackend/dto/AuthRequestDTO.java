package com.example.portfoliobackend.dto;

public class AuthRequestDTO {
    private String identifier;
    private String password;

    public AuthRequestDTO() {
    }

    public AuthRequestDTO(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
