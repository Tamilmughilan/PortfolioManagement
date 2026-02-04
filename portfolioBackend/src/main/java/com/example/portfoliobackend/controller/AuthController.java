package com.example.portfoliobackend.controller;

import com.example.portfoliobackend.dto.AuthRequestDTO;
import com.example.portfoliobackend.dto.AuthResponseDTO;
import com.example.portfoliobackend.dto.SignupRequestDTO;
import com.example.portfoliobackend.entity.User;
import com.example.portfoliobackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDTO> signup(@RequestBody SignupRequestDTO request) {
        if (request.getUsername() == null || request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        User created = userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getDefaultCurrency()
        );
        if (created == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(toAuthResponse(created));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        if (request.getIdentifier() == null || request.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        User user = userService.authenticate(request.getIdentifier(), request.getPassword());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(toAuthResponse(user));
    }

    private AuthResponseDTO toAuthResponse(User user) {
        return new AuthResponseDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getDefaultCurrency(),
                user.getCreatedAt()
        );
    }
}
