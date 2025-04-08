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
import org.springframework.security.test.context.support.WithMockUser;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    private User getOrCreateTestUser() {
        User existingUser = userRepository.findByUsername("testuser");
        if (existingUser != null) {
            return existingUser;
        } else {
            User newUser = new User("testuser", "password123");
            return userRepository.save(newUser);
        }
    }

    @Test
    @WithMockUser
    void addExpense() throws Exception {
        User user = getOrCreateTestUser();

        mockMvc.perform(post("/api/expenses/post")
                .contentType("application/json")
                .content("{\"title\": \"Groceries\", \"amount\": \"234.5\", \"userId\": \"" + user.getId() + 
                        "\", \"category\": \"food\", \"createdDate\": \"2020-10-05\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Expense saved successfully!"));
    }

    @Test
    @WithMockUser
    void getExpensesByUser() throws Exception {
        User user = getOrCreateTestUser();

        if (expenseRepository.findByUserId(user.getId()).isEmpty()) {
            expenseRepository.save(new Expense("Groceries", 234.5, user.getId(), "food", LocalDate.now()));
        }

        mockMvc.perform(get("/api/expenses/user/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Groceries"))
                .andExpect(jsonPath("$[0].amount").value(234.5));
    }

    @Test
    @WithMockUser
    void updateExpense() throws Exception {
        User user = getOrCreateTestUser();

        List<Expense> expenses = expenseRepository.findByUserId(user.getId());
        Expense expense = expenses.isEmpty() ? 
            expenseRepository.save(new Expense("Groceries", 234.5, user.getId(), "food", LocalDate.now())) :
            expenses.get(0);

        mockMvc.perform(put("/api/expenses/" + expense.getId())
                .contentType("application/json")
                .content("{\"title\": \"Updated Groceries\", \"amount\": \"150.00\", \"userId\": \"" + user.getId() + 
                        "\", \"category\": \"food\", \"createdDate\": \"2023-10-05\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Expense updated successfully!"));
    }

    @Test
    @WithMockUser
    void deleteExpense() throws Exception {
        User user = getOrCreateTestUser();

        List<Expense> expenses = expenseRepository.findByUserId(user.getId());
        Expense expense = expenses.isEmpty() ? 
            expenseRepository.save(new Expense("Groceries", 234.5, user.getId(), "food", LocalDate.now())) :
            expenses.get(0);

        mockMvc.perform(delete("/api/expenses/" + expense.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Expense deleted successfully!"));
    }
}