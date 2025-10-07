package com.example.bankcards.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import com.example.bankcards.service.UserServiceImpl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    	
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== BANK CARD MANAGEMENT SYSTEM ===");
        createDefaultUsers();
        log.info("Default users created successfully!");
        log.info("Admin: admin / admin123");
        log.info("User:  user / user123");
        log.info("===================================");
    }

    private void createDefaultUsers() {
        // Create admin user
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User(
                "admin",
                passwordEncoder.encode("admin123"),
                "admin@bank.com",
                "System",
                "Administrator",
                Role.ROLE_ADMIN
            );
            userRepository.save(admin);
            log.info("Admin user created: admin / admin123");
        }

        // Create test user
        if (!userRepository.existsByUsername("user")) {
            User user = new User(
                "user",
                passwordEncoder.encode("user123"),
                "user@bank.com",
                "John",
                "Doe",
                Role.ROLE_USER
            );
            userRepository.save(user);
            log.info("Test user created: user / user123");
        }
    }
}
