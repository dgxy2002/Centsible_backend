package com.example.backendapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "testExpenses") // Maps this class to the MongoDB collection "expenses"
public class Expense {
    @Id // Marks this field as the primary key
    private String id;
    private String title;
    private String amount;
    private String userId; // Reference to the user who created this expense
    private String category; // New field for expense category
    private LocalDate createdDate; // New field for creation date

    public Expense(String title, String amount, String userId, String category) {
        this.title = title;
        this.amount = amount;
        this.userId = userId;
        this.category = category;
        this.createdDate = LocalDate.now(); 
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
   
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    // toString() method (optional, for debugging)
    @Override
    public String toString() {
        return "Expense{" +
                "id='" + id + '\'' +
                ", amount='" + amount + '\'' +
                ", userId='" + userId + '\'' +
                ", category='" + category + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}