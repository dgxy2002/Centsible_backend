package com.example.backendapi.repository;

import com.example.backendapi.model.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;

import java.util.List;

public interface ExpenseRepository extends MongoRepository<Expense, String> {
    // Custom query to find all expenses for a specific user
    List<Expense> findByUserId(String userId);

    // Custom query to calculate total expenses for a specific user
    @Aggregation(pipeline = {
        "{ $match: { userId: ?0 } }",
        "{ $group: { _id: null, total: { $sum: { $toDouble: '$amount' } } } }"
    })
    Double getTotalExpensesByUserId(String userId);

    // Custom query to calculate total expenses by category for a specific user
    @Aggregation(pipeline = {
        "{ $match: { userId: ?0 } }", // Match stage: Filter by userId
        "{ $group: { " +
            "_id: '$category', " + // Group by category
            "total: { $sum: { $toDouble: '$amount' } }, " + // Sum the amount field
            "category: { $first: '$category' } " + // Retain the category field
        "} }"
    })
    List<CategoryTotal> getTotalExpensesByCategoryForUser(String userId);

    @Aggregation(pipeline = {
        "{ $match: { userId: ?0, category: ?1 } }",
        "{ $group: { _id: null, total: { $sum: { $toDouble: '$amount' } } } }"
    })
    Double getTotalExpensesByCategoryForUser(String userId, String category);    

    // Interface to represent the result of the aggregation
    interface CategoryTotal {
        String getCategory(); // Category name
        Double getTotal();    // Total amount for the category
    }
}