package com.example.backendapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "testIncomes")
public class Income {
    @Id // Marks this field as the primary key
    private String id;
    private String title;
    private Double amount;
    private String userId;
    private LocalDate createdDate;

    public Income(String title, Double amount, String userId, LocalDate createdDate) {
        this.title = title;
        this.amount = amount;
        this.userId = userId;
        this.createdDate = createdDate;;
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Income{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", amount='" + amount + '\'' +
                ", userId='" + userId + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }

}