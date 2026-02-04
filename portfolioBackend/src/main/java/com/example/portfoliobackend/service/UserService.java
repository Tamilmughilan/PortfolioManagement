package com.example.portfoliobackend.service;



import com.example.portfoliobackend.entity.User;
import com.example.portfoliobackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final String DEFAULT_CURRENCY = "INR";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public User createUser(User user) {
        if (user.getDefaultCurrency() == null) {
            user.setDefaultCurrency(DEFAULT_CURRENCY);
        }
        return userRepository.save(user);
    }

    @Transactional
    public User registerUser(String username, String email, String rawPassword, String defaultCurrency) {
        if (userRepository.findByEmail(email).isPresent()) {
            return null;
        }
        if (userRepository.findByUsername(username).isPresent()) {
            return null;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setDefaultCurrency(defaultCurrency != null ? defaultCurrency : DEFAULT_CURRENCY);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        return userRepository.save(user);
    }

    public User authenticate(String identifier, String rawPassword) {
        if (identifier == null || rawPassword == null) {
            return null;
        }
        Optional<User> userOpt = identifier.contains("@")
                ? userRepository.findByEmail(identifier)
                : userRepository.findByUsername(identifier);

        if (!userOpt.isPresent()) {
            return null;
        }
        User user = userOpt.get();
        if (user.getPasswordHash() == null) {
            return null;
        }
        return passwordEncoder.matches(rawPassword, user.getPasswordHash()) ? user : null;
    }

    @Transactional
    public User updateUser(Long id, User updated) {
        Optional<User> existing = userRepository.findById(id);
        if (!existing.isPresent()) {
            return null;
        }

        User user = existing.get();
        if (updated.getUsername() != null) {
            user.setUsername(updated.getUsername());
        }
        if (updated.getEmail() != null) {
            user.setEmail(updated.getEmail());
        }
        if (updated.getDefaultCurrency() != null) {
            user.setDefaultCurrency(updated.getDefaultCurrency());
        }
        return userRepository.save(user);
    }

    @Transactional
    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }
}