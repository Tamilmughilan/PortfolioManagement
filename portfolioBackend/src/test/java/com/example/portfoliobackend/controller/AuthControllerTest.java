package com.example.portfoliobackend.controller;

import com.example.portfoliobackend.dto.AuthRequestDTO;
import com.example.portfoliobackend.dto.SignupRequestDTO;
import com.example.portfoliobackend.entity.User;
import com.example.portfoliobackend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@DisplayName("AuthController Integration Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setDefaultCurrency("USD");
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("POST /api/auth/signup - Should return 201 and AuthResponseDTO on success")
    void signup_Success_ShouldReturnCreated() throws Exception {
        SignupRequestDTO request = new SignupRequestDTO("testuser", "test@example.com", "password123", "USD");
        when(userService.registerUser(anyString(), anyString(), anyString(), anyString())).thenReturn(testUser);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.defaultCurrency").value("USD"))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(userService, times(1))
                .registerUser("testuser", "test@example.com", "password123", "USD");
    }

    @Test
    @DisplayName("POST /api/auth/signup - Should return 400 when required fields are missing")
    void signup_MissingRequiredFields_ShouldReturnBadRequest() throws Exception {
        SignupRequestDTO request = new SignupRequestDTO(null, "test@example.com", "password123", "USD");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, times(0)).registerUser(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("POST /api/auth/signup - Should return 409 when user already exists")
    void signup_Conflict_ShouldReturnConflict() throws Exception {
        SignupRequestDTO request = new SignupRequestDTO("testuser", "test@example.com", "password123", "USD");
        when(userService.registerUser(anyString(), anyString(), anyString(), anyString())).thenReturn(null);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(userService, times(1))
                .registerUser("testuser", "test@example.com", "password123", "USD");
    }

    @Test
    @DisplayName("POST /api/auth/login - Should return 200 and AuthResponseDTO on success")
    void login_Success_ShouldReturnOk() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("testuser", "password123");
        when(userService.authenticate("testuser", "password123")).thenReturn(testUser);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.defaultCurrency").value("USD"))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(userService, times(1)).authenticate("testuser", "password123");
    }

    @Test
    @DisplayName("POST /api/auth/login - Should return 400 when identifier is missing")
    void login_MissingIdentifier_ShouldReturnBadRequest() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO(null, "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, times(0)).authenticate(anyString(), anyString());
    }

    @Test
    @DisplayName("POST /api/auth/login - Should return 400 when password is missing")
    void login_MissingPassword_ShouldReturnBadRequest() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("testuser", null);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, times(0)).authenticate(anyString(), anyString());
    }

    @Test
    @DisplayName("POST /api/auth/login - Should return 401 on invalid credentials")
    void login_InvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("testuser", "wrongpass");
        when(userService.authenticate("testuser", "wrongpass")).thenReturn(null);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(userService, times(1)).authenticate("testuser", "wrongpass");
    }
}