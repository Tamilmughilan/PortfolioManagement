package com.example.portfoliobackend.controller;

import com.example.portfoliobackend.entity.User;
import com.example.portfoliobackend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@DisplayName("UserController Integration Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private User testUser;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setDefaultCurrency("USD");
        testUser.setCreatedAt(LocalDateTime.now());

        testUser2 = new User();
        testUser2.setUserId(2L);
        testUser2.setUsername("user2");
        testUser2.setEmail("user2@example.com");
        testUser2.setDefaultCurrency("INR");
        testUser2.setCreatedAt(LocalDateTime.now());
    }

    // ==================== GET ALL USERS ====================

    @Test
    @DisplayName("GET /api/users - Should return all users")
    void getAllUsers_ShouldReturnUserList() throws Exception {
        List<User> users = Arrays.asList(testUser, testUser2);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is("testuser")))
                .andExpect(jsonPath("$[0].email", is("test@example.com")))
                .andExpect(jsonPath("$[1].username", is("user2")))
                .andExpect(jsonPath("$[1].email", is("user2@example.com")));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("GET /api/users - Should return empty list when no users")
    void getAllUsers_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("GET /api/users - Should return single user list")
    void getAllUsers_WithOneUser_ShouldReturnSingleItemList() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is(1)));

        verify(userService, times(1)).getAllUsers();
    }

    // ==================== GET USER BY ID ====================

    @Test
    @DisplayName("GET /api/users/{id} - Should return user when exists")
    void getUserById_WhenExists_ShouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.defaultCurrency", is("USD")));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return 404 when not exists")
    void getUserById_WhenNotExists_ShouldReturn404() throws Exception {
        when(userService.getUserById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return user with different ID")
    void getUserById_WithDifferentId_ShouldReturnCorrectUser() throws Exception {
        when(userService.getUserById(2L)).thenReturn(testUser2);

        mockMvc.perform(get("/api/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(2)))
                .andExpect(jsonPath("$.username", is("user2")));

        verify(userService, times(1)).getUserById(2L);
    }

    // ==================== CREATE USER ====================

    @Test
    @DisplayName("POST /api/users - Should create user")
    void createUser_ShouldReturnCreatedUser() throws Exception {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");

        User savedUser = new User();
        savedUser.setUserId(1L);
        savedUser.setUsername("newuser");
        savedUser.setEmail("new@example.com");
        savedUser.setDefaultCurrency("INR");

        when(userService.createUser(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.username", is("newuser")))
                .andExpect(jsonPath("$.email", is("new@example.com")))
                .andExpect(jsonPath("$.defaultCurrency", is("INR")));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    @DisplayName("POST /api/users - Should create user with custom currency")
    void createUser_WithCustomCurrency_ShouldReturnUser() throws Exception {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setDefaultCurrency("USD");

        User savedUser = new User();
        savedUser.setUserId(1L);
        savedUser.setUsername("newuser");
        savedUser.setEmail("new@example.com");
        savedUser.setDefaultCurrency("USD");

        when(userService.createUser(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.defaultCurrency", is("USD")));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    @DisplayName("POST /api/users - Should create user with minimal data")
    void createUser_WithMinimalData_ShouldReturnCreatedUser() throws Exception {
        User newUser = new User();
        newUser.setUsername("minimaluser");
        newUser.setEmail("minimal@example.com");

        User savedUser = new User();
        savedUser.setUserId(3L);
        savedUser.setUsername("minimaluser");
        savedUser.setEmail("minimal@example.com");
        savedUser.setDefaultCurrency("INR");
        savedUser.setCreatedAt(LocalDateTime.now());

        when(userService.createUser(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", is(3)))
                .andExpect(jsonPath("$.createdAt", notNullValue()));

        verify(userService, times(1)).createUser(any(User.class));
    }

    // ==================== UPDATE USER ====================

    @Test
    @DisplayName("PUT /api/users/{id} - Should update user when exists")
    void updateUser_WhenExists_ShouldReturnUpdatedUser() throws Exception {
        User updateData = new User();
        updateData.setUsername("updateduser");
        updateData.setEmail("updated@example.com");

        User updatedUser = new User();
        updatedUser.setUserId(1L);
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setDefaultCurrency("USD");

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.username", is("updateduser")))
                .andExpect(jsonPath("$.email", is("updated@example.com")));

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Should return 404 when not exists")
    void updateUser_WhenNotExists_ShouldReturn404() throws Exception {
        User updateData = new User();
        updateData.setUsername("updateduser");

        when(userService.updateUser(eq(999L), any(User.class))).thenReturn(null);

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(eq(999L), any(User.class));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Should update only username")
    void updateUser_OnlyUsername_ShouldReturnUpdatedUser() throws Exception {
        User updateData = new User();
        updateData.setUsername("newusername");

        User updatedUser = new User();
        updatedUser.setUserId(1L);
        updatedUser.setUsername("newusername");
        updatedUser.setEmail("test@example.com");
        updatedUser.setDefaultCurrency("USD");

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("newusername")))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Should update only email")
    void updateUser_OnlyEmail_ShouldReturnUpdatedUser() throws Exception {
        User updateData = new User();
        updateData.setEmail("newemail@example.com");

        User updatedUser = new User();
        updatedUser.setUserId(1L);
        updatedUser.setUsername("testuser");
        updatedUser.setEmail("newemail@example.com");
        updatedUser.setDefaultCurrency("USD");

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.email", is("newemail@example.com")));

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Should update currency")
    void updateUser_WithCurrency_ShouldReturnUpdatedUser() throws Exception {
        User updateData = new User();
        updateData.setDefaultCurrency("EUR");

        User updatedUser = new User();
        updatedUser.setUserId(1L);
        updatedUser.setUsername("testuser");
        updatedUser.setEmail("test@example.com");
        updatedUser.setDefaultCurrency("EUR");

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.defaultCurrency", is("EUR")));

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    // ==================== DELETE USER ====================

    @Test
    @DisplayName("DELETE /api/users/{id} - Should delete user when exists")
    void deleteUser_WhenExists_ShouldReturn204() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Should return 404 when not exists")
    void deleteUser_WhenNotExists_ShouldReturn404() throws Exception {
        when(userService.deleteUser(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser(999L);
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Should delete different user")
    void deleteUser_WithDifferentId_ShouldReturn204() throws Exception {
        when(userService.deleteUser(2L)).thenReturn(true);

        mockMvc.perform(delete("/api/users/2"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(2L);
    }

    // ==================== EDGE CASES ====================

    @Test
    @DisplayName("GET /api/users/{id} - Should handle zero ID")
    void getUserById_WithZeroId_ShouldReturn404() throws Exception {
        when(userService.getUserById(0L)).thenReturn(null);

        mockMvc.perform(get("/api/users/0"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(0L);
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Should handle zero ID")
    void deleteUser_WithZeroId_ShouldReturn404() throws Exception {
        when(userService.deleteUser(0L)).thenReturn(false);

        mockMvc.perform(delete("/api/users/0"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser(0L);
    }
}
