package com.example.backendapi.migration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.backendapi.repository.UserRepository;
import com.example.backendapi.model.User;

import java.time.LocalDate;
import java.util.List;

@Component
public class UserMigrationRunner implements CommandLineRunner {

    @Autowired
    private UserRepository userRepo;

    @Value("${default.profile.image}")
    private String defaultProfileImage;

    @Value("${enable.migration:false}")
    private boolean enableMigration;

    @Override
    public void run(String... args) throws Exception {
        if (!enableMigration) return;

        List<User> users = userRepo.findAll();

        for (User user : users) {
            boolean changed = false;

            if (user.getFirstname() == null) {
                user.setFirstname(""); // or derive from 'name' if needed
                changed = true;
            }

            if (user.getLastname() == null) {
                user.setLastname("");
                changed = true;
            }

            if (user.getBiography() == null) {
                user.setBiography("");
                changed = true;
            }

            if (user.getBirthdate() == null) {
                user.setBirthdate(null); // or LocalDate.of(2000, 1, 1) for dummy value
                changed = true;
            }

            if (user.getImageUrl() == null || user.getImageUrl().isEmpty()) {
                user.setImageUrl(defaultProfileImage);
                changed = true;
            }

            if (changed) {
                userRepo.save(user);
            }
        }

        System.out.println("✅ Migration complete: added missing fields to existing users.");
    }
}
