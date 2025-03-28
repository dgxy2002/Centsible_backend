package com.example.backendapi.config; // Adjust package name

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for API
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/register").permitAll() // Open registration
                .requestMatchers("/api/users/login").permitAll() // Open login
                .anyRequest().authenticated() // Secure other endpoints
            );
        
        return http.build();
    }
}