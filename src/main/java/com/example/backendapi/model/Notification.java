// Notification.java
package com.example.backendapi.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document
public class Notification {
    @Id
    private String id;
    private String userId; // Recipient ID
    private String message;
    private String senderUsername;
    private LocalDateTime createdAt = LocalDateTime.now();
    private boolean read = false;

    public void setRead(boolean read) {
        this.read = read;
    }
}