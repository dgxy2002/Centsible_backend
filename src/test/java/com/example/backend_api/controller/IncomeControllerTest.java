package com.example.backendapi.controller;

import com.example.backendapi.model.Income;
import com.example.backendapi.repository.IncomeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

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
    void addIncome() throws Exception {
        // Test adding a new income
        mockMvc.perform(post("/api/incomes")
                .contentType("application/json")
                .content("{\"title\": \"Salary\", \"amount\": \"5000.00\", \"userId\": \"67d3d20a29d0cd06ab44add8\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Income saved successfully!"));
    }

    @Test
    void getIncomesByUser() throws Exception {
        // Add a test income
        Income income = new Income("Salary", "5000.00", "67d3d20a29d0cd06ab44add8");
        incomeRepository.save(income);

        // Test retrieving incomes for a user
        mockMvc.perform(get("/api/incomes/user/67d3d20a29d0cd06ab44add8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Salary"))
                .andExpect(jsonPath("$[0].amount").value("5000.00"));
    }

    @Test
    void getIncomeById() throws Exception {
        // Add a test income
        Income income = new Income("Salary", "5000.00", "67d3d20a29d0cd06ab44add8");
        incomeRepository.save(income);

        // Test retrieving an income by ID
        mockMvc.perform(get("/api/incomes/" + income.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Salary"))
                .andExpect(jsonPath("$.amount").value("5000.00"));
    }

    @Test
    void updateIncome() throws Exception {
        // Add a test income
        Income income = new Income("Salary", "5000.00", "67d3d20a29d0cd06ab44add8");
        incomeRepository.save(income);

        // Test updating the income
        mockMvc.perform(put("/api/incomes/" + income.getId())
                .contentType("application/json")
                .content("{\"title\": \"Bonus\", \"amount\": \"1000.00\", \"userId\": \"67d3d20a29d0cd06ab44add8\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Income updated successfully!"));
    }

    @Test
    void deleteIncome() throws Exception {
        // Add a test income
        Income income = new Income("Salary", "5000.00", "67d3d20a29d0cd06ab44add8");
        incomeRepository.save(income);

        // Test deleting the income
        mockMvc.perform(delete("/api/incomes/" + income.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Income deleted successfully!"));
    }
}