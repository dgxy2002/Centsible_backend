package com.example.backendapi.repository;

import com.example.backendapi.model.Income;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncomeRepository extends MongoRepository<Income, String> {
    // Custom query to find all incomes for a specific user
    List<Income> findByUserId(String userId);
}