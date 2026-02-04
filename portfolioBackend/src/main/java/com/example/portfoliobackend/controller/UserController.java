package com.example.portfoliobackend.controller;



import com.example.portfoliobackend.dto.UserDTO;
import com.example.portfoliobackend.entity.User;
import com.example.portfoliobackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> results = userService.getAllUsers().stream()
                .map(UserController::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(toDTO(user));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user) {
        User toCreate = new User();
        toCreate.setUsername(user.getUsername());
        toCreate.setEmail(user.getEmail());
        toCreate.setDefaultCurrency(user.getDefaultCurrency());
        User created = userService.createUser(toCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO user) {
        User toUpdate = new User();
        toUpdate.setUsername(user.getUsername());
        toUpdate.setEmail(user.getEmail());
        toUpdate.setDefaultCurrency(user.getDefaultCurrency());
        User updated = userService.updateUser(id, toUpdate);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userService.deleteUser(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }

    private static UserDTO toDTO(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getDefaultCurrency(),
                user.getCreatedAt()
        );
    }
}