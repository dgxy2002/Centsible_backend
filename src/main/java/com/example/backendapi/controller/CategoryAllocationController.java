package com.example.backendapi.controller;

import com.example.backendapi.model.CategoryAllocation;
import com.example.backendapi.repository.CategoryAllocationRepository;
import com.example.backendapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.backendapi.repository.ExpenseRepository; 
import java.util.List;

@RestController
@RequestMapping("/api/category-allocations")
public class CategoryAllocationController {

    @Autowired
    private CategoryAllocationRepository categoryAllocationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    // Add or update a category allocation for a user
    @PostMapping
    public ResponseEntity<String> addOrUpdateCategoryAllocation(@RequestBody List<CategoryAllocation> allocations) {
        
        for (CategoryAllocation allocation : allocations) {
            // Check if an allocation already exists for this user and category
            CategoryAllocation existingAllocation = categoryAllocationRepository.findByUserIdAndCategory(
                    allocation.getUserId(), allocation.getCategory());

            if (existingAllocation != null) {
                // Update the existing allocation
                existingAllocation.setAllocatedAmount(allocation.getAllocatedAmount());
                categoryAllocationRepository.save(existingAllocation);
            } else {
                Double totalExpenses = expenseRepository.getTotalExpensesByCategoryForUser(allocation.getUserId(), allocation.getCategory());
                double value = totalExpenses != null ? totalExpenses.doubleValue() : 0.0;
                allocation.setSpentAmount(value);
                categoryAllocationRepository.save(allocation);
            }
        }
        return new ResponseEntity<>("Category allocation updated successfully!", HttpStatus.OK);
    }

    // Get all category allocations for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CategoryAllocation>> getCategoryAllocationsByUser(@PathVariable String userId) {
        // Check if the user exists
        if (userRepository.findById(userId).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Fetch all allocations for the user
        List<CategoryAllocation> allocations = categoryAllocationRepository.findByUserId(userId);
        return new ResponseEntity<>(allocations, HttpStatus.OK);
    }

    // Delete a category allocation by ID
    @DeleteMapping("/{allocationId}")
    public ResponseEntity<String> deleteCategoryAllocation(@PathVariable String allocationId) {
        // Check if the allocation exists
        if (categoryAllocationRepository.findById(allocationId).isEmpty()) {
            return new ResponseEntity<>("Category allocation not found", HttpStatus.NOT_FOUND);
        }

        // Delete the allocation
        categoryAllocationRepository.deleteById(allocationId);
        return new ResponseEntity<>("Category allocation deleted successfully!", HttpStatus.OK);
    }
}