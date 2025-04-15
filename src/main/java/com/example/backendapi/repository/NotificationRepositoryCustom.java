package com.example.backendapi.repository;
import java.time.LocalDate;

public interface NotificationRepositoryCustom {
    void markAllAsRead(String userId, LocalDate sinceDate);
}
