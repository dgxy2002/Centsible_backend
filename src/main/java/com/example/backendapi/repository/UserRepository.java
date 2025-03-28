package com.example.backendapi.repository;

import com.example.backendapi.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    // Custom query to find a user by username
    User findByUsername(String username);
    User findByUsernameOrEmail(String username, String email);

    @Aggregation(pipeline = {
        "{ $group: { _id: '$username', score: { $sum: '$score' }, username: { $first: '$username' }, email: { $first: '$email' }, budget: { $first: '$budget' } } }",
        "{ $sort: { score: -1 } }",
        "{ $limit: 10 }"
    })
    List<User> findTopUsersByScore();
}