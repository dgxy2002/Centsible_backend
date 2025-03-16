package com.example.backendapi.controller;

import com.example.backendapi.model.Expense;
import com.example.backendapi.model.User;
import com.example.backendapi.repository.ExpenseRepository;
import com.example.backendapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void addExpense() throws Exception {
        // Add a test user
        User user = new User("testuser", "test@example.com");
        userRepository.save(user);

        // Test adding a new expense
        mockMvc.perform(post("/api/expenses/post")
                .contentType("application/json")
                .content("{\"title\": \"Groceries\", \"amount\": \"100.50\", \"userId\": \"" + user.getId() + "\", \"category\": \"food\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Expense saved successfully!"));
    }

    @Test
    void getExpensesByUser() throws Exception {
        // Add a test user and expense
        User user = new User("testuser", "test@example.com");
        userRepository.save(user);

        Expense expense = new Expense("Groceries", "100.50", user.getId(), "food"); // No need to pass createdDate
        expenseRepository.save(expense);

        // Test retrieving expenses for a user
        mockMvc.perform(get("/api/expenses/user/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Groceries"))
                .andExpect(jsonPath("$[0].amount").value("100.50"));
    }

    @Test
    void updateExpense() throws Exception {
        // Add a test user and expense
        User user = new User("testuser", "test@example.com");
        userRepository.save(user);

        Expense expense = new Expense("Groceries", "100.50", user.getId(), "food"); // No need to pass createdDate
        expenseRepository.save(expense);

        // Test updating the expense
        mockMvc.perform(put("/api/expenses/" + expense.getId())
                .contentType("application/json")
                .content("{\"title\": \"Updated Groceries\", \"amount\": \"150.00\", \"userId\": \"" + user.getId() + "\", \"category\": \"food\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Expense updated successfully!"));
    }

    @Test
    void deleteExpense() throws Exception {
        // Add a test user and expense
        User user = new User("testuser", "test@example.com");
        userRepository.save(user);

        Expense expense = new Expense("Groceries", "100.50", user.getId(), "food"); // No need to pass createdDate
        expenseRepository.save(expense);

        // Test deleting the expense
        mockMvc.perform(delete("/api/expenses/" + expense.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Expense deleted successfully!"));
    }
}