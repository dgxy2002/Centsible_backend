package com.example.backendapi.controller;

import com.example.backendapi.model.Income;
import com.example.backendapi.model.User;
import com.example.backendapi.repository.IncomeRepository;
import com.example.backendapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.time.LocalDate; 
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/incomes")
public class IncomeController {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    // Add a new income for a specific user
    @PostMapping
    public ResponseEntity<String> addIncome(@RequestBody Income income) {
        // Check if the user exists
        User user = userRepository.findById(income.getUserId()).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Save the income
        incomeRepository.save(income);
        return new ResponseEntity<>("Income saved successfully!", HttpStatus.CREATED);
    }

    // Get all incomes for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Income>> getIncomesByUser(@PathVariable String userId) {
        // Check if the user exists
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Fetch incomes for the user
        List<Income> incomes = incomeRepository.findByUserId(userId);
        return new ResponseEntity<>(incomes, HttpStatus.OK);
    }

    // Get a specific income by ID
    @GetMapping("/{incomeId}")
    public ResponseEntity<Income> getIncomeById(@PathVariable String incomeId) {
        Income income = incomeRepository.findById(incomeId).orElse(null);
        if (income == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(income, HttpStatus.OK);
    }

    // Update an income
    @PutMapping("/{incomeId}")
    public ResponseEntity<String> updateIncome(@PathVariable String incomeId, @RequestBody Income updatedIncome) {
        Income income = incomeRepository.findById(incomeId).orElse(null);
        if (income == null) {
            return new ResponseEntity<>("Income not found", HttpStatus.NOT_FOUND);
        }

        // Update the income fields
        income.setTitle(updatedIncome.getTitle());
        income.setAmount(updatedIncome.getAmount());
        income.setUserId(updatedIncome.getUserId());
        income.setCreatedDate(updatedIncome.getCreatedDate());

        // Save the updated income
        incomeRepository.save(income);
        return new ResponseEntity<>("Income updated successfully!", HttpStatus.OK);
    }

    // Delete an income
    @DeleteMapping("/{incomeId}")
    public ResponseEntity<String> deleteIncome(@PathVariable String incomeId) {
        Income income = incomeRepository.findById(incomeId).orElse(null);
        if (income == null) {
            return new ResponseEntity<>("Income not found", HttpStatus.NOT_FOUND);
        }

        // Delete the income
        incomeRepository.deleteById(incomeId);
        return new ResponseEntity<>("Income deleted successfully!", HttpStatus.OK);
    }

    // Get all income logs for a specific user and month
    @GetMapping("/user/{userId}/month/{month}")
    public ResponseEntity<List<Income>> getIncomeByUserAndMonth(@PathVariable String userId,
            @PathVariable int month) {
        // Check if the user exists
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Fetch all expenses for the user
        List<Income> allIncome = incomeRepository.findByUserId(userId);

        // Filter expenses for the given month
        List<Income> incomeForMonth = allIncome.stream()
                .filter(income -> income.getCreatedDate().getMonthValue() == month) // Filter by month
                .collect(Collectors.toList());

        return new ResponseEntity<>(incomeForMonth, HttpStatus.OK);
    }

    // Get all expenses for a specific user and the current month
    @GetMapping("/user/{userId}/current-month")
    public ResponseEntity<List<Income>> getIncomeByUserForCurrentMonth(@PathVariable String userId) {
        // Check if the user exists
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Get the current month
        int currentMonth = LocalDate.now().getMonthValue();

        // Fetch all expenses for the user
        List<Income> allIncome = incomeRepository.findByUserId(userId);

        // Filter expenses for the current month
        List<Income> incomeForCurrentMonth = allIncome.stream()
                .filter(income -> income.getCreatedDate().getMonthValue() == currentMonth) // Filter by current month
                .collect(Collectors.toList());

        return new ResponseEntity<>(incomeForCurrentMonth, HttpStatus.OK);
    }

    // Get all expenses for a specific user, year, and month
    @GetMapping("/user/{userId}/year/{year}/month/{month}")
    public ResponseEntity<List<Income>> getIncomeByUserYearAndMonth(
            @PathVariable String userId,
            @PathVariable int year,
            @PathVariable int month) {
        
        // Check if the user exists
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Fetch all expenses for the user
        List<Income> allIncome = incomeRepository.findByUserId(userId);

        // Filter expenses for the given year and month
        List<Income> incomeForMonth = allIncome.stream()
                .filter(income -> income.getCreatedDate().getYear() == year &&
                                income.getCreatedDate().getMonthValue() == month)
                .collect(Collectors.toList());

        return new ResponseEntity<>(incomeForMonth, HttpStatus.OK);
    }
}