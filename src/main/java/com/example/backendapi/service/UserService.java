package com.example.backendapi.service;

import com.example.backendapi.model.User;
import com.example.backendapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Authenticate user and update login streak
    public User login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username);
        
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        updateLoginStreak(user);
        return user;
    }

    // Update login streak logic
    private void updateLoginStreak(User user) {
        LocalDate today = LocalDate.now();
        LocalDate lastLogin = user.getLastLoginDate();

        if (lastLogin == null) {
            user.setLoginStreak(1);
        } else if (lastLogin.isEqual(today.minusDays(1))) {
            user.setLoginStreak(user.getLoginStreak() + 1);
        } else if (!lastLogin.isEqual(today)) {
            user.setLoginStreak(1);
        }

        user.setLastLoginDate(today);
        userRepository.save(user);
    }

    // Create/register a new user (with password hashing)
    public User createUser(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }
}