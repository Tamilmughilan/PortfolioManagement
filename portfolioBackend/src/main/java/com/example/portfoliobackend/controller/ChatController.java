package com.example.portfoliobackend.controller;

import com.example.portfoliobackend.dto.ChatRequestDTO;
import com.example.portfoliobackend.dto.ChatResponseDTO;
import com.example.portfoliobackend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponseDTO> chat(@RequestBody ChatRequestDTO request) {
        String reply = chatService.chat(request);
        return ResponseEntity.ok(new ChatResponseDTO(reply));
    }
}
