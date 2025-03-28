package com.example.backendapi.controller;

import com.example.backendapi.model.User;
import com.example.backendapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
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
        // Test adding a new user with password
        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content("{\"username\": \"testuser\", \"email\": \"test@example.com\", \"password\": \"secure123\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("User saved successfully!"));
    }

    @Test
    void getUserById() throws Exception {
        // Add a test user with password (plaintext for test purposes)
        User user = new User("testuser", "test@example.com", "secure123");
        userRepository.save(user);

        // Test retrieving a user by ID (password should be excluded from response)
        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void addConnection() throws Exception {
        // Add two test users with passwords (plaintext for test purposes)
        User user1 = new User("user1", "user1@example.com", "password1");
        User user2 = new User("user2", "user2@example.com", "password2");
        userRepository.save(user1);
        userRepository.save(user2);

        // Test adding a connection between users
        mockMvc.perform(post("/api/users/" + user1.getId() + "/connections/" + user2.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Connection added successfully!"));
    }

    @Test
    void getConnections() throws Exception {
        // Add a test user with connections and password (plaintext for test purposes)
        User user = new User("testuser", "test@example.com", "secure123");
        user.addConnection("67d3d20a29d0cd06ab44add8");
        userRepository.save(user);

        // Test retrieving connections for a user
        mockMvc.perform(get("/api/users/" + user.getId() + "/connections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("67d3d20a29d0cd06ab44add8"));
    }

    @Test
    void updateBudget() throws Exception {
        // Add a test user with password (plaintext for test purposes)
        User user = new User("testuser", "test@example.com", "secure123");
        userRepository.save(user);

        // Test updating the user's budget
        mockMvc.perform(put("/api/users/" + user.getId() + "/budget")
                .param("budget", "1000.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("Budget updated successfully!"));
    }

    @Test
    void getTopUsersByScore() throws Exception {
        // Add test users with scores and passwords (plaintext for test purposes)
        User user1 = new User("TEST_user1", "TEST_user1@example.com", "password1");
        user1.setScore(100);
        userRepository.save(user1);
    
        User user2 = new User("TEST_user2", "TEST_user2@example.com", "password2");
        user2.setScore(200);
        userRepository.save(user2);
    
        // Test retrieving top users by score
        mockMvc.perform(get("/api/users/top-users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("ryan_teo"))
                .andExpect(jsonPath("$[0].score").value(30000))
                .andExpect(jsonPath("$[1].username").value("john_doe"))
                .andExpect(jsonPath("$[1].score").value(20000));
    }

    @Test
    void loginUser() throws Exception {
        // Add a test user with plaintext password (hashing will be done by service layer)
        User user = new User("testuser", "test@example.com", "secure123");
        userRepository.save(user);

        // Test user login
        mockMvc.perform(post("/api/users/login")
                .contentType("application/json")
                .content("{\"usernameOrEmail\": \"testuser\", \"password\": \"secure123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.streak").exists());
    }
}