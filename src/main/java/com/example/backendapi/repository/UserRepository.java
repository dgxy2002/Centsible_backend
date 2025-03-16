package com.example.backendapi.repository;

import com.example.backendapi.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    // Custom query to find a user by username
    User findByUsername(String username);
}