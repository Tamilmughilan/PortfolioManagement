package com.example.portfoliobackend.controller;

import com.example.portfoliobackend.dto.MarketNewsDTO;
import com.example.portfoliobackend.service.MarketNewsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MarketController.class)
@DisplayName("MarketController Integration Tests")
class MarketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MarketNewsService marketNewsService;

    @Test
    @DisplayName("GET /api/market/news - Should return list of market news")
    void getMarketNews_ShouldReturnNewsList() throws Exception {
        List<MarketNewsDTO> news = Arrays.asList(
                new MarketNewsDTO("Headline 1", "Source 1", "http://example.com/1", 123L, "Summary 1", "http://img/1"),
                new MarketNewsDTO("Headline 2", "Source 2", "http://example.com/2", 456L, "Summary 2", "http://img/2")
        );
        when(marketNewsService.getLatestNews()).thenReturn(news);

        mockMvc.perform(get("/api/market/news"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].headline").value("Headline 1"))
                .andExpect(jsonPath("$[0].source").value("Source 1"))
                .andExpect(jsonPath("$[0].url").value("http://example.com/1"))
                .andExpect(jsonPath("$[0].datetime").value(123))
                .andExpect(jsonPath("$[0].summary").value("Summary 1"))
                .andExpect(jsonPath("$[0].image").value("http://img/1"));

        verify(marketNewsService, times(1)).getLatestNews();
    }

    @Test
    @DisplayName("GET /api/market/news - Should return empty list when no news available")
    void getMarketNews_Empty_ShouldReturnEmptyList() throws Exception {
        when(marketNewsService.getLatestNews()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/market/news"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(marketNewsService, times(1)).getLatestNews();
    }
}