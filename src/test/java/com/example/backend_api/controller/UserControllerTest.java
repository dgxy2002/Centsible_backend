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
import org.springframework.data.mongodb.core.MongoTemplate;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.context.annotation.Import;
import com.example.backendapi.config.SecurityConfig;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class) // Import your security configuration
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void loginUser() throws Exception {
        //mongoTemplate.dropCollection("testUniqueUsers");

        String username = "testuserSpecific";
        String rawPassword = "secure123";
        
        User user = new User(username, passwordEncoder.encode(rawPassword));
        userRepository.save(user);
    
        // System.out.println("DEBUG: Expected username -> " + user.getUsername());
        // System.out.println("DEBUG: Expected raw password -> " + rawPassword);
        // System.out.println("DEBUG: Expected encoded password -> " + passwordEncoder.encode(rawPassword));
    
        // Test successful login
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"usernameOrEmail\":\"%s\",\"password\":\"%s\"}", username, rawPassword)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnauthorized());

    }
}
