package com.example.portfoliobackend.controller;

import com.example.portfoliobackend.dto.ChatRequestDTO;
import com.example.portfoliobackend.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@DisplayName("ChatController Integration Tests")
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatService chatService;

    @Test
    @DisplayName("POST /api/chat - Should return reply wrapped in ChatResponseDTO")
    void chat_ShouldReturnReply() throws Exception {
        when(chatService.chat(any())).thenReturn("Hello! This is a reply.");

        ChatRequestDTO request = new ChatRequestDTO("Hi", 1L);

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reply").value("Hello! This is a reply."));

        verify(chatService, times(1)).chat(any());
    }

    @Test
    @DisplayName("POST /api/chat - Should accept null portfolioId")
    void chat_NullPortfolioId_ShouldReturnReply() throws Exception {
        when(chatService.chat(any())).thenReturn("General reply");

        ChatRequestDTO request = new ChatRequestDTO("What is a stock?", null);

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("General reply"));

        verify(chatService, times(1)).chat(any());
    }

    @Test
    @DisplayName("POST /api/chat - Should accept missing message field (service decides response)")
    void chat_MissingMessageField_ShouldReturnReply() throws Exception {
        when(chatService.chat(any())).thenReturn("Ask me anything about stocks, markets, or your portfolio.");

        // Only portfolioId present
        String body = "{\"portfolioId\":1}";

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("Ask me anything about stocks, markets, or your portfolio."));

        verify(chatService, times(1)).chat(any());
    }
}