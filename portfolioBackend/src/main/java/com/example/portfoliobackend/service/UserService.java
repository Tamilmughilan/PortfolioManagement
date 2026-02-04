package com.example.portfoliobackend.service;



import com.example.portfoliobackend.entity.User;
import com.example.portfoliobackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final String DEFAULT_CURRENCY = "INR";

    @Autowired
    private UserRepository userRepository;

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