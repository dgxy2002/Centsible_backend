package com.example.backendapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.backendapi.repository.UserRepository;
import com.example.backendapi.repository.ExpenseRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import java.util.Optional;

@Document(collection = "categoryAllocations")
public class CategoryAllocation {
    @Id
    private String id;
    private String userId;
    private String category;
    private double allocatedAmount;
    private double spentAmount = 0.0; 

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    public CategoryAllocation(String userId, String category, double allocatedAmount) {
        this.userId = userId;
        this.category = category;
        this.allocatedAmount = allocatedAmount;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(double allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public void setSpentAmount(double spentAmount) {
        this.spentAmount = spentAmount;
    }

    public double getSpentAmount() {
        return spentAmount;
    }
}