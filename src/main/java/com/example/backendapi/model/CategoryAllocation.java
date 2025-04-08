package com.example.backendapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categoryAllocations")
public class CategoryAllocation {
    @Id
    private String id;
    private String userId;
    private String category;
    private double allocatedAmount;
    // amount spent in the category

    // Constructors
    public CategoryAllocation() {}

    public CategoryAllocation(String userId, String category, double allocatedAmount) {
        this.userId = userId;
        this.category = category;
        this.allocatedAmount = allocatedAmount;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public double getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(double allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    @Override
    public String toString() {
        return "CategoryAllocation{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", category='" + category + '\'' +
                ", allocatedAmount=" + allocatedAmount +
                '}';
    }
}