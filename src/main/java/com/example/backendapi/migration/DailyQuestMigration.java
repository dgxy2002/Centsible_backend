// UserNudgeLogMigrationRunner.java
package com.example.backendapi.migration;

import com.example.backendapi.model.User;
import com.example.backendapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DailyQuestMigration implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Value("${enable.nudge.log.migration:false}")
    private boolean enableMigration;

    @Override
    public void run(String... args) throws Exception {
        if (!enableMigration) {
            System.out.println("⚠️ Migration skipped: enable.migration.nudge.log is false.");
            return;
        }

        List<User> users = userRepository.findAll();
        int updatedCount = 0;

        for (User user : users) {
            boolean changed = false;

            if (user.getLastNudge() == null) {
                user.setLastNudge(LocalDate.of(2000, 1, 1));
                changed = true;
            }

            if (user.getLastLog() == null) {
                user.setLastLog(LocalDate.of(2000, 1, 1));
                changed = true;
            }

            if (changed) {
                userRepository.save(user);
                updatedCount++;
            }
        }

        System.out.println("✅ Migration complete: updated " + updatedCount + " users with default lastNudge and lastLog.");
    }
}
