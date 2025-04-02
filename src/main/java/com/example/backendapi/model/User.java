package com.example.backendapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.mongodb.core.index.Indexed; // Import Indexed annotation
import java.time.LocalDate;

import java.util.Map; 

@Document(collection = "testUniqueUsers") // Maps this class to the MongoDB collection "users"
public class User {
    @Id // Marks this field as the primary key
    private String id;
    @Indexed(unique=true)
    private String username;
    private String password;
    private List<Map<String,String>> connections = new ArrayList<>(); // Field for user connections
    private List<Map<String, String>> pendingInvitations = new ArrayList<>();
    private double budget;
    private int score;
    private LocalDate lastLoginDate;
    private int loginStreak;
    private List<String> parentId;

    // Constructors
    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.score = 0;
        this.budget = 0.0;
        this.loginStreak = 1;
        this.lastLoginDate = LocalDate.now();
        this.parentId = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Map<String, String>> getConnections() {
        return connections;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public void addConnection(String userId, String username) {
        this.connections.add(Map.of(userId, username));
    }

    public void removeConnection(String userId, String username) {
        this.connections.remove(Map.of(userId, username));
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    // Method to increment the score
    public void incrementScore(int points) {
        this.score += points;
    }

    // Method to decrement the score
    public void decrementScore(int points) {
        this.score -= points;
    }

    public LocalDate getLastLoginDate() {
        return this.lastLoginDate;
    }

    public void setLastLoginDate(LocalDate newLoginDate) {
        this.lastLoginDate = newLoginDate;
    }

    public int getLoginStreak() {
        return this.loginStreak;
    }

    public void setLoginStreak(int newLoginStreak) {
        this.loginStreak = newLoginStreak;
    }

    public double getRemainingBudget(List<CategoryAllocation> allocations) {
        double totalAllocated = allocations.stream()
                .mapToDouble(CategoryAllocation::getAllocatedAmount)
                .sum();
        return this.budget - totalAllocated;
    }

    public void addPendingInvitation(String userId, String username) {
        if (!this.pendingInvitations.contains(Map.of(userId, username))) {
            this.pendingInvitations.add(Map.of(userId, username));
        }
    }

    public List<Map<String, String>> getPendingInvitations() {
        return this.pendingInvitations;
    }
    
    public void removePendingInvitation(String userId, String username) {
        this.pendingInvitations.remove(Map.of(userId, username));
    }

    public List<String> getParentId() {
        if (this.parentId == null){
            return null;
        } else {
            return this.parentId;
        }
    }

    public void addParentId(String parentId) {
        this.parentId.add(parentId);
    }

    // toString() method (optional, for debugging)
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", connections=" + connections +
                ", budget=" + budget +
                '}';
    }
}