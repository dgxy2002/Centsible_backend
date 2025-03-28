package com.example.backendapi.repository;

import com.example.backendapi.model.CategoryAllocation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CategoryAllocationRepository extends MongoRepository<CategoryAllocation, String> {
    // Find all allocations for a specific user
    List<CategoryAllocation> findByUserId(String userId);

    // Find an allocation for a specific user and category
    CategoryAllocation findByUserIdAndCategory(String userId, String category);
}