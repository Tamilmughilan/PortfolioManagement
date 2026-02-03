package com.example.portfoliobackend.repository;

import com.example.portfoliobackend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Integration Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setDefaultCurrency("USD");
    }

    @Test
    @DisplayName("Should save user successfully")
    void save_ShouldPersistUser() {
        User savedUser = userRepository.save(testUser);

        assertThat(savedUser.getUserId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find user by ID")
    void findById_WhenExists_ShouldReturnUser() {
        User persisted = entityManager.persistFlushFind(testUser);

        Optional<User> found = userRepository.findById(persisted.getUserId());

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should return empty when user not found")
    void findById_WhenNotExists_ShouldReturnEmpty() {
        Optional<User> found = userRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find user by email")
    void findByEmail_WhenExists_ShouldReturnUser() {
        entityManager.persistFlushFind(testUser);

        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void findByEmail_WhenNotExists_ShouldReturnEmpty() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find all users")
    void findAll_ShouldReturnAllUsers() {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
    }

    @Test
    @DisplayName("Should update user successfully")
    void save_ShouldUpdateExistingUser() {
        User persisted = entityManager.persistFlushFind(testUser);

        persisted.setUsername("updateduser");
        persisted.setEmail("updated@example.com");
        userRepository.save(persisted);
        entityManager.flush();
        entityManager.clear();

        User updated = entityManager.find(User.class, persisted.getUserId());

        assertThat(updated.getUsername()).isEqualTo("updateduser");
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @DisplayName("Should delete user successfully")
    void delete_ShouldRemoveUser() {
        User persisted = entityManager.persistFlushFind(testUser);
        Long userId = persisted.getUserId();

        userRepository.deleteById(userId);
        entityManager.flush();

        User deleted = entityManager.find(User.class, userId);
        assertThat(deleted).isNull();
    }

    @Test
    @DisplayName("Should check if user exists by ID")
    void existsById_WhenExists_ShouldReturnTrue() {
        User persisted = entityManager.persistFlushFind(testUser);

        boolean exists = userRepository.existsById(persisted.getUserId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when checking non-existent user")
    void existsById_WhenNotExists_ShouldReturnFalse() {
        boolean exists = userRepository.existsById(999L);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should set default currency when not provided")
    void save_WithoutCurrency_ShouldUseDefault() {
        User userWithoutCurrency = new User();
        userWithoutCurrency.setUsername("nocurrency");
        userWithoutCurrency.setEmail("nocurrency@example.com");
        // defaultCurrency will use entity default "INR"

        User saved = userRepository.save(userWithoutCurrency);

        assertThat(saved.getDefaultCurrency()).isEqualTo("INR");
    }
}

