package com.example.backendapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "testUsers") // Maps this class to the MongoDB collection "users"
public class User {
    @Id // Marks this field as the primary key
    private String id;
    private String username;
    private String email;
    private List<String> connections = new ArrayList<>(); // Field for user connections
    private double budget;
    private int score;

    // Constructors
    public User() {}

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.score = 0;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getConnections() {
        return connections;
    }

    public void setConnections(List<String> connections) {
        this.connections = connections;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public void addConnection(String userId) {
        this.connections.add(userId);
    }

    public void removeConnection(String userId) {
        this.connections.remove(userId);
    }

    public int getScore() {
        return this.score;
    }

    // Private setter for score (only accessible within the class)
    private void setScore(int score) {
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

    // toString() method (optional, for debugging)
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", connections=" + connections +
                ", budget=" + budget +
                '}';
    }
}