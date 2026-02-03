package com.example.portfoliobackend.service;

import com.example.portfoliobackend.entity.User;
import com.example.portfoliobackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
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
    @DisplayName("Should return all users")
    void getAllUsers_ShouldReturnAllUsers() {
        User user2 = new User();
        user2.setUserId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");

        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result).contains(testUser, user2);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return user by ID when exists")
    void getUserById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return null when user not found")
    void getUserById_WhenUserNotExists_ShouldReturnNull() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        User result = userService.getUserById(999L);

        assertThat(result).isNull();
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create user with default currency when not provided")
    void createUser_WithoutCurrency_ShouldSetDefaultCurrency() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setUserId(1L);
            return saved;
        });

        User result = userService.createUser(newUser);

        assertThat(result.getDefaultCurrency()).isEqualTo("INR");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should create user with provided currency")
    void createUser_WithCurrency_ShouldKeepProvidedCurrency() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setDefaultCurrency("EUR");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setUserId(1L);
            return saved;
        });

        User result = userService.createUser(newUser);

        assertThat(result.getDefaultCurrency()).isEqualTo("EUR");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user when exists")
    void updateUser_WhenUserExists_ShouldUpdateAndReturnUser() {
        User updatedData = new User();
        updatedData.setUsername("updateduser");
        updatedData.setEmail("updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.updateUser(1L, updatedData);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("updateduser");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should return null when updating non-existent user")
    void updateUser_WhenUserNotExists_ShouldReturnNull() {
        User updatedData = new User();
        updatedData.setUsername("updateduser");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        User result = userService.updateUser(999L, updatedData);

        assertThat(result).isNull();
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user when exists")
    void deleteUser_WhenUserExists_ShouldReturnTrue() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        boolean result = userService.deleteUser(1L);

        assertThat(result).isTrue();
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should return false when deleting non-existent user")
    void deleteUser_WhenUserNotExists_ShouldReturnFalse() {
        when(userRepository.existsById(999L)).thenReturn(false);

        boolean result = userService.deleteUser(999L);

        assertThat(result).isFalse();
        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }
}