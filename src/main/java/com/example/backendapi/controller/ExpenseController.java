package com.example.backendapi.controller;

import com.example.backendapi.model.Expense;
import com.example.backendapi.model.User;
import com.example.backendapi.model.Notification;
import com.example.backendapi.model.CategoryAllocation;
import com.example.backendapi.repository.NotificationRepository;
import com.example.backendapi.repository.ExpenseRepository;
import com.example.backendapi.repository.CategoryAllocationRepository;
import com.example.backendapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backendapi.repository.ExpenseRepository.CategoryTotal; // Import CategoryTotal

import java.time.LocalDate; 
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private CategoryAllocationRepository categoryAllocationRepository;

    // Add a new expense for a specific user
    @PostMapping("/post")
    public ResponseEntity<String> addExpense(@RequestBody Expense expense) {
        // Check if the user exists
        User user = userRepository.findById(expense.getUserId()).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        user.incrementScore(5);
        userRepository.save(user); 

        // Notify all connections
        notifyConnections(user, expense);
        updateAllocationSpent(user, expense);

        // Save the expense
        expenseRepository.save(expense);
        return new ResponseEntity<>("Expense saved successfully!", HttpStatus.CREATED);
    }

    private void notifyConnections(User user, Expense expense) {
        // Only notify if this user has a parent
        if (user.getParentId() != null) {
            for (String parentId : user.getParentId()){
                Notification notification = new Notification();
                notification.setUserId(parentId); // Notify the parent
                notification.setMessage(user.getUsername() + " logged a new expense: " + 
                    expense.getTitle() + " ($" + expense.getAmount() + ")");
                notification.setSenderUsername(user.getUsername());
                
                notificationRepository.save(notification);
            }
        }
    }

    private void updateAllocationSpent(User user, Expense expense) {
        String category = expense.getCategory();
        CategoryAllocation allocation = categoryAllocationRepository.findByUserIdAndCategory(user.getId(), category);
        if (allocation != null) {
            // Update the spent amount for the category
            allocation.setSpentAmount(allocation.getSpentAmount() + expense.getAmount());
            categoryAllocationRepository.save(allocation);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Expense>> getExpensesByUser(@PathVariable String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Fetch expenses for the user
        List<Expense> expenses = expenseRepository.findByUserId(userId);
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    // Update an expense by ID
    @PutMapping("/{expenseId}")
    public ResponseEntity<String> updateExpense(
            @PathVariable String expenseId,
            @RequestBody Expense updatedExpense) {
        // Check if the expense exists
        Expense expense = expenseRepository.findById(expenseId).orElse(null);
        if (expense == null) {
            return new ResponseEntity<>("Expense not found", HttpStatus.NOT_FOUND);
        }

        // Update the expense fields
        expense.setTitle(updatedExpense.getTitle());
        expense.setAmount(updatedExpense.getAmount());
        expense.setUserId(updatedExpense.getUserId());
        expense.setCategory(updatedExpense.getCategory());
        expense.setCreatedDate(updatedExpense.getCreatedDate());

        // Save the updated expense
        expenseRepository.save(expense);

        return new ResponseEntity<>("Expense updated successfully!", HttpStatus.OK);
    }

    // Delete an expense by ID
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<String> deleteExpense(@PathVariable String expenseId) {
        // Check if the expense exists
        Expense expense = expenseRepository.findById(expenseId).orElse(null);
        if (expense == null) {
            return new ResponseEntity<>("Expense not found", HttpStatus.NOT_FOUND);
        }

        // Delete the expense
        expenseRepository.deleteById(expenseId);

        return new ResponseEntity<>("Expense deleted successfully!", HttpStatus.OK);
    }

    // Get all expenses for a specific user and month
    @GetMapping("/user/{userId}/month/{month}")
    public ResponseEntity<List<Expense>> getExpensesByUserAndMonth(@PathVariable String userId,
            @PathVariable int month) {
        // Check if the user exists
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Fetch all expenses for the user
        List<Expense> allExpenses = expenseRepository.findByUserId(userId);

        // Filter expenses for the given month
        List<Expense> expensesForMonth = allExpenses.stream()
                .filter(expense -> expense.getCreatedDate().getMonthValue() == month) // Filter by month
                .collect(Collectors.toList());

        return new ResponseEntity<>(expensesForMonth, HttpStatus.OK);
    }

    // Get all expenses for a specific user, year, and month
    @GetMapping("/user/{userId}/year/{year}/month/{month}")
    public ResponseEntity<List<Expense>> getExpensesByUserYearAndMonth(
            @PathVariable String userId,
            @PathVariable int year,
            @PathVariable int month) {
        
        // Check if the user exists
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Fetch all expenses for the user
        List<Expense> allExpenses = expenseRepository.findByUserId(userId);

        // Filter expenses for the given year and month
        List<Expense> expensesForMonth = allExpenses.stream()
                .filter(expense -> expense.getCreatedDate().getYear() == year &&
                                expense.getCreatedDate().getMonthValue() == month)
                .collect(Collectors.toList());

        return new ResponseEntity<>(expensesForMonth, HttpStatus.OK);
    }

    // Get all expenses for a specific user in a given year
    @GetMapping("/user/{userId}/year/{year}")
    public ResponseEntity<List<Expense>> getExpensesByUserAndYear(
            @PathVariable String userId,
            @PathVariable int year) {
        
        // Check if the user exists
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Fetch all expenses for the user
        List<Expense> allExpenses = expenseRepository.findByUserId(userId);

        // Filter expenses for the given year
        List<Expense> expensesForYear = allExpenses.stream()
                .filter(expense -> expense.getCreatedDate().getYear() == year)
                .collect(Collectors.toList());

        return new ResponseEntity<>(expensesForYear, HttpStatus.OK);
    }


    // Get all expenses for a specific user and the current month
    @GetMapping("/user/{userId}/current-month")
    public ResponseEntity<List<Expense>> getExpensesByUserForCurrentMonth(@PathVariable String userId) {
        // Check if the user exists
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Get the current month
        int currentMonth = LocalDate.now().getMonthValue();

        // Fetch all expenses for the user
        List<Expense> allExpenses = expenseRepository.findByUserId(userId);

        // Filter expenses for the current month
        List<Expense> expensesForCurrentMonth = allExpenses.stream()
                .filter(expense -> expense.getCreatedDate().getMonthValue() == currentMonth) // Filter by current month
                .collect(Collectors.toList());

        return new ResponseEntity<>(expensesForCurrentMonth, HttpStatus.OK);
    }

    // Get total expenses for a specific user
    @GetMapping("/user/{userId}/total")
    public ResponseEntity<Double> getTotalExpensesByUser(@PathVariable String userId) {
        // Check if the user exists
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Calculate total expenses for the user
        Double totalExpenses = expenseRepository.getTotalExpensesByUserId(userId);
        return new ResponseEntity<>(totalExpenses, HttpStatus.OK);
    }

    // Get total expenses by category for a specific user
    @GetMapping("/user/{userId}/total-by-category")
    public ResponseEntity<?> getTotalExpensesByCategoryForUser(@PathVariable String userId) {
        // Check if the user exists
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    
        // Calculate total expenses by category for the user
        List<ExpenseRepository.CategoryTotal> categoryTotals = expenseRepository.getTotalExpensesByCategoryForUser(userId);
    
        try {
            Map<String, Double> result = categoryTotals.stream()
                    .collect(Collectors.toMap(
                            categoryTotal -> categoryTotal.getCategory() == null ? "Uncategorized" : categoryTotal.getCategory(), // Handle null categories
                            ExpenseRepository.CategoryTotal::getTotal,
                            (total1, total2) -> total1 + total2 // Merge duplicates by summing their totals
                    ));
    
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred while processing your request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{userId}/percent-per-category")
    public ResponseEntity<?> getPercentByCategoryForUser(@PathVariable String userId) {
        // Check if the user exists
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    
        // Get the user's budget
        Double budget = user.getBudget();
    
        // Handle zero budget
        if (budget == 0) {
            return new ResponseEntity<>("Budget is zero", HttpStatus.OK);
        }
    
        // Calculate total expenses by category for the user
        List<ExpenseRepository.CategoryTotal> categoryTotals = expenseRepository.getTotalExpensesByCategoryForUser(userId);
    
        // Create a mutable map to store the results
        Map<String, Double> result = new HashMap<>();
    
        try {
            // Calculate the percentage of budget used for each category
            for (ExpenseRepository.CategoryTotal category : categoryTotals) {
                String categoryName = category.getCategory() == null ? "Others" : category.getCategory();
                double percentage = (category.getTotal() / budget) * 100;
                result.put(categoryName, percentage);
            }
    
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred while processing your request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}