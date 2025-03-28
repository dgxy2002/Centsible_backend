package com.example.backendapi.controller;

import com.example.backendapi.model.User;
import com.example.backendapi.service.UserService;
import com.example.backendapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.backendapi.repository.ExpenseRepository; // Import ExpenseRepository

import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ExpenseRepository expenseRepository; // Autowire ExpenseRepository

    // Add a new user
    @PostMapping
    public ResponseEntity<String> addUser(@RequestBody User user) {
        userRepository.save(user);
        return new ResponseEntity<>("User saved successfully!", HttpStatus.CREATED);
    }

    // Get a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Add a connection between two users
    @PostMapping("/{userId}/connections/{connectedUserId}")
    public ResponseEntity<String> addConnection(@PathVariable String userId, @PathVariable String connectedUserId) {
        User user = userRepository.findById(userId).orElse(null);
        User connectedUser = userRepository.findById(connectedUserId).orElse(null);

        if (user == null || connectedUser == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Add the connection
        user.addConnection(connectedUserId);
        userRepository.save(user);

        return new ResponseEntity<>("Connection added successfully!", HttpStatus.OK);
    }

    // Get all connections for a user
    @GetMapping("/{userId}/connections")
    public ResponseEntity<List<String>> getConnections(@PathVariable String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user.getConnections(), HttpStatus.OK);
    }

    // Update a user's budget
    @PutMapping("/{userId}/budget")
    public ResponseEntity<String> updateBudget(@PathVariable String userId, @RequestParam double budget) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Update the budget
        user.setBudget(budget);
        userRepository.save(user);

        return new ResponseEntity<>("Budget updated successfully!", HttpStatus.OK);
    }

    // Get how much of the budget has a user used, returned in percentage (total expenses / budget * 100)
    @GetMapping("/{userId}/budget-usage")
    public ResponseEntity<Double> getBudgetUsagePercentage(@PathVariable String userId) {
        // Check if the user exists
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Get the user's budget
        double budget = user.getBudget();

        // Calculate total expenses for the user
        Double totalExpenses = expenseRepository.getTotalExpensesByUserId(userId);

        // Handle zero budget
        if (budget == 0) {
            return new ResponseEntity<>(0.0, HttpStatus.OK);
        }

        // If there are no expenses, return 0%
        if (totalExpenses == null || totalExpenses == 0) {
            return new ResponseEntity<>(0.0, HttpStatus.OK);
        }

        // Calculate the percentage of budget used
        double budgetUsagePercentage = (totalExpenses / budget) * 100;

        return new ResponseEntity<>(budgetUsagePercentage, HttpStatus.OK);
    }

        // Get a user's score
    @GetMapping("/{userId}/score")
    public ResponseEntity<Integer> getScore(@PathVariable String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user.getScore(), HttpStatus.OK);
    }

    // Increment a user's score
    @PostMapping("/{userId}/increment-score")
    public ResponseEntity<String> incrementScore(@PathVariable String userId, @RequestParam int points) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Increment the score
        user.incrementScore(points);
        userRepository.save(user);

        return new ResponseEntity<>("Score incremented successfully!", HttpStatus.OK);
    }

    // Decrement a user's score
    @PostMapping("/{userId}/decrement-score")
    public ResponseEntity<String> decrementScore(@PathVariable String userId, @RequestParam int points) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Decrement the score
        user.decrementScore(points);
        userRepository.save(user);

        return new ResponseEntity<>("Score decremented successfully!", HttpStatus.OK);
    }
    
    @GetMapping("/top-users")
    public List<User> getTopUsersByScore() {
        return userRepository.findTopUsersByScore();
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request.getUsernameOrEmail(), request.getPassword());
            return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "streak", user.getLoginStreak()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // Registration endpoint
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User newUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
}

class LoginRequest {
    private String usernameOrEmail;
    private String password;

    // Getters and setters
    public String getUsernameOrEmail() { return usernameOrEmail; }
    public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}