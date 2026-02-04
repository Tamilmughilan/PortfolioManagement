package com.example.portfoliobackend.controller;

import com.example.portfoliobackend.dto.MarketNewsDTO;
import com.example.portfoliobackend.service.MarketNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/market")
@CrossOrigin
public class MarketController {

    @Autowired
    private MarketNewsService marketNewsService;

    @GetMapping("/news")
    public ResponseEntity<List<MarketNewsDTO>> getMarketNews() {
        return ResponseEntity.ok(marketNewsService.getLatestNews());
    }
}
