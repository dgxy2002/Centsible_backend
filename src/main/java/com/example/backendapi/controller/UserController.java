package com.example.backendapi.controller;

import com.example.backendapi.model.User;
import com.example.backendapi.service.UserService;
import com.example.backendapi.repository.UserRepository;
import com.example.backendapi.model.Notification;
import com.example.backendapi.repository.NotificationRepository;
import com.example.backendapi.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.backendapi.repository.ExpenseRepository; 
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.core.Authentication;

import com.example.backendapi.security.JwtTokenProvider;

import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ExpenseRepository expenseRepository; 

    @Autowired
    private NotificationRepository notificationRepository;

    // Add a new user
    @PostMapping
    public ResponseEntity<String> addUser(@RequestBody User user) {
        userService.createUser(user);
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

    // Send connection invitation
    @PostMapping("/{inviterUsername}/invite/{inviteeUsername}")
    public ResponseEntity<String> sendConnectionInvitation(
            @PathVariable String inviterUsername,
            @PathVariable String inviteeUsername) {
        
        // Check if users exist
        User inviterObj = userRepository.findByUsername(inviterUsername);
        User inviteeObj = userRepository.findByUsername(inviteeUsername);

        String inviterId = inviterObj != null ? inviterObj.getId() : null;
        String inviteeId = inviteeObj != null ? inviteeObj.getId() : null;
        
        if (inviterId == null || inviteeId == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        
        // Prevent self-invitation
        if (inviterUsername.equals(inviteeUsername)) {
            return new ResponseEntity<>("Cannot invite yourself", HttpStatus.BAD_REQUEST);
        }
        
        // Check if already connected
        if (inviteeObj.getConnections().contains(Map.of(inviterId, inviterUsername))) {
            return new ResponseEntity<>("Already connected", HttpStatus.BAD_REQUEST);
        }
        
        // Check if invitation already exists
        if (inviteeObj.getPendingInvitations().contains(Map.of(inviterId, inviterUsername))) {
            return new ResponseEntity<>("Invitation already sent", HttpStatus.BAD_REQUEST);
        }
        
        // Add to pending invitations
        inviteeObj.addPendingInvitation(inviterId, inviterUsername);
        userRepository.save(inviteeObj);
        
        return new ResponseEntity<>("Invitation sent successfully", HttpStatus.OK);
    }

    // Respond to invitation
    @PutMapping("/{inviteeUsername}/respond-invitation/{inviterUsername}")
    public ResponseEntity<String> respondToInvitation(
            @PathVariable String inviteeUsername,
            @PathVariable String inviterUsername,
            @RequestParam boolean accept) {
        
        User invitee = userRepository.findByUsername(inviteeUsername);
        User inviter = userRepository.findByUsername(inviterUsername);

        String inviterId = inviter != null ? inviter.getId() : null;
        String inviteeId = invitee != null ? invitee.getId() : null;
        
        if (invitee == null || inviter == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        
        if (!invitee.getPendingInvitations().contains(Map.of(inviterId, inviterUsername))) {
            return new ResponseEntity<>("No pending invitation", HttpStatus.BAD_REQUEST);
        }
        
        // Remove from pending regardless of response
        invitee.removePendingInvitation(inviterId, inviterUsername);
        userRepository.save(invitee);
        
        if (accept) {
            inviter.addConnection(inviteeId, inviteeUsername);
            invitee.addParentId(inviterId);
            userRepository.saveAll(List.of(invitee, inviter));
        }
        
        return new ResponseEntity<>(accept ? "Invitation accepted" : "Invitation declined", HttpStatus.OK);
    }

    // Get all connections for a user
    @GetMapping("/{userId}/connections")
    public ResponseEntity<List<Map<String, String>>> getConnections(@PathVariable String userId) {
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

    // Remove a connection
    @DeleteMapping("/{userId}/connections/{connectionId}")
    public ResponseEntity<String> removeConnection(
            @PathVariable String userId,
            @PathVariable String connectionId) {
        
        User user = userRepository.findById(userId).orElse(null);
        User connection = userRepository.findById(connectionId).orElse(null);
        
        if (user == null || connection == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        
        if (!user.getConnections().contains(Map.of(connectionId, connection.getUsername()))) {
            return new ResponseEntity<>("Connection does not exist", HttpStatus.BAD_REQUEST);
        }
        
        // Remove mutual connection
        user.removeConnection(connectionId, connection.getUsername());
        connection.getParentId().remove(userId); // Remove parent ID if it's the user being removed
        userRepository.save(user);
        
        return new ResponseEntity<>("Connection removed successfully", HttpStatus.OK);
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
            User user = userService.login(request.getUsername(), request.getPassword());
            String token = jwtTokenProvider.generateToken(user.getUsername());
            
            return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "token", token,
                "id", user.getId(),
                "username", user.getUsername()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // Registration endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // Check if username already exists
            if (userRepository.existsByUsername(user.getUsername())) {
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Username already exists"));
            }
            
            User newUser = new User(user.getUsername(), passwordEncoder.encode(user.getPassword()));
            userRepository.save(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User registered successfully"));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Object>> checkUsernameAvailability(
        @PathVariable String username) {
        boolean isAvailable = !userRepository.existsByUsername(username);
        
        return ResponseEntity.ok(Map.of(
            "username", username,
            "available", isAvailable
        ));
    }

    @GetMapping("/{userId}/notifications") 
    public ResponseEntity<List<Notification>> getRecentNotifications(
        @PathVariable String userId) { 
        
        // // Authentication check
        // if (!authenticatedUserMatches(userId, authentication)) {
        //     return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        // }
        
        LocalDate oneWeekAgo = LocalDate.now().minusDays(7);
        List<Notification> notifications = notificationRepository
            .findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(userId, oneWeekAgo);
        for (Notification notification : notifications) {
            notification.setRead(true); // Mark as read
            notificationRepository.save(notification); // Save the updated notification
        }
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{userId}/pending-invitations")
    public ResponseEntity<List<Map<String, String>>> getPendingInvitations(
            @PathVariable String userId) {
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(user.getPendingInvitations(), HttpStatus.OK);
    }

    // Get unread count
    @GetMapping("/{userId}/notifications/unread-count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable String userId) {
        long count = notificationRepository.countByUserIdAndReadFalse(userId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/{fromUsername}/nudge/{toUsername}")
    public ResponseEntity<String> sendNudge(
            @PathVariable String fromUsername,
            @PathVariable String toUsername) {
        
        // Check if user exists
        User toUser = userRepository.findByUsername(toUsername);
        User fromUser = userRepository.findByUsername(fromUsername);
        if (fromUser == null || toUser == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        
        String message = "Hey " + toUsername + "! " + fromUsername + " has nudged you, log an expense for the day!";

        // Create and save notification
        Notification notification = new Notification();
        notification.setUserId(toUser.getId());
        notification.setMessage(message);
        notification.setSenderUsername(fromUsername); // or the sender's username
        notificationRepository.save(notification);

        fromUser.incrementScore(5);
        userRepository.save(fromUser);
        
        return new ResponseEntity<>("Nudge sent successfully", HttpStatus.OK);
    }
}

class LoginRequest {
    private String username;
    private String password;

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}