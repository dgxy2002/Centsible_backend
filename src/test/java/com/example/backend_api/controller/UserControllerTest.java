package com.example.backendapi.controller;

import com.example.backendapi.model.User;
import com.example.backendapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void addUser() throws Exception {
        // Test adding a new user
        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content("{\"username\": \"testuser\", \"email\": \"test@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("User saved successfully!"));
    }

    @Test
    void getUserById() throws Exception {
        // Add a test user
        User user = new User("testuser", "test@example.com");
        userRepository.save(user);

        // Test retrieving a user by ID
        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void addConnection() throws Exception {
        // Add two test users
        User user1 = new User("user1", "user1@example.com");
        User user2 = new User("user2", "user2@example.com");
        userRepository.save(user1);
        userRepository.save(user2);

        // Test adding a connection between users
        mockMvc.perform(post("/api/users/" + user1.getId() + "/connections/" + user2.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Connection added successfully!"));
    }

    @Test
    void getConnections() throws Exception {
        // Add a test user with connections
        User user = new User("testuser", "test@example.com");
        user.addConnection("67d3d20a29d0cd06ab44add8");
        userRepository.save(user);

        // Test retrieving connections for a user
        mockMvc.perform(get("/api/users/" + user.getId() + "/connections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("67d3d20a29d0cd06ab44add8"));
    }

    @Test
    void updateBudget() throws Exception {
        // Add a test user
        User user = new User("testuser", "test@example.com");
        userRepository.save(user);

        // Test updating the user's budget
        mockMvc.perform(put("/api/users/" + user.getId() + "/budget")
                .param("budget", "1000.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("Budget updated successfully!"));
    }
}