// UserCleanupMigrationRunner.java
package com.example.backendapi.migration;

import com.example.backendapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class TestUserCleanUp implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Value("${enable.cleanup:false}")
    private boolean enableCleanup;

    @Override
    public void run(String... args) throws Exception {
        if (!enableCleanup) return;
        String usernameToDelete = "testuserSpecific";

        long deletedCount = userRepository.deleteByUsername(usernameToDelete);
        
        if (deletedCount > 0) {
            System.out.println("Removed " + deletedCount + " user(s) with username: " + usernameToDelete);
        } else {
            System.out.println("No users found with username: " + usernameToDelete);
        }
    }
}
