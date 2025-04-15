package com.example.backendapi.repository;

import com.example.backendapi.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface NotificationRepository 
    extends MongoRepository<Notification, String>, NotificationRepositoryCustom {
    
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    
    List<Notification> findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(
        String userId, LocalDate date
    );
    
    long countByUserIdAndReadFalse(String userId);
}
