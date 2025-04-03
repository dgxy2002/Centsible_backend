package com.example.backendapi.controller;

import com.example.backendapi.model.Income;
import com.example.backendapi.repository.IncomeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;

import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IncomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IncomeRepository incomeRepository;

    @Test
    @WithMockUser
    void addIncome() throws Exception {
        // Test adding a new income with createdDate
        mockMvc.perform(post("/api/incomes")
                .contentType("application/json")
                .content("{\"title\": \"Salary\", \"amount\": \"5000.00\", \"userId\": \"67d3d20a29d0cd06ab44add8\", \"createdDate\": \"2023-10-05\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    @WithMockUser
    void getIncomesByUser() throws Exception {
        // Add a test income with createdDate
        Income income = new Income("Salary", 5000.00, "67d3d20a29d0cd06ab44add8", LocalDate.now());
        incomeRepository.save(income);

        // Test retrieving incomes for a user
        mockMvc.perform(get("/api/incomes/user/67d3d20a29d0cd06ab44add8"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getIncomeById() throws Exception {
        // Add a test income with createdDate
        Income income = new Income("Salary", 5000.00, "67d3d20a29d0cd06ab44add8", LocalDate.now());
        incomeRepository.save(income);

        // Test retrieving an income by ID
        mockMvc.perform(get("/api/incomes/" + income.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Salary"))
                .andExpect(jsonPath("$.amount").value(5000.00))
                .andExpect(jsonPath("$.createdDate").exists());
    }

    @Test
    @WithMockUser
    void updateIncome() throws Exception {
        // Add a test income with createdDate
        Income income = new Income("Salary", 5000.00, "67d3d20a29d0cd06ab44add8", LocalDate.now());
        incomeRepository.save(income);

        // Test updating the income (include createdDate in update)
        mockMvc.perform(put("/api/incomes/" + income.getId())
                .contentType("application/json")
                .content("{\"title\": \"Bonus\", \"amount\": \"1000.00\", \"userId\": \"67d3d20a29d0cd06ab44add8\", \"createdDate\": \"2023-10-05\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Income updated successfully!"));
    }

    @Test
    @WithMockUser
    void deleteIncome() throws Exception {
        // Add a test income with createdDate
        Income income = new Income("Salary", 5000.00, "67d3d20a29d0cd06ab44add8", LocalDate.now());
        incomeRepository.save(income);

        // Test deleting the income
        mockMvc.perform(delete("/api/incomes/" + income.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Income deleted successfully!"));
    }
}