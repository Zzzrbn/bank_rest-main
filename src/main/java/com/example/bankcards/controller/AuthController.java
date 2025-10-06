package com.example.bankcards.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @PostMapping("/login")
    public Map<String, Object> login(Authentication authentication) {
        return Map.of(
            "status", "success",
            "message", "Login successful",
            "username", authentication.getName(),
            "role", authentication.getAuthorities().iterator().next().getAuthority(),
            "authenticated", true
        );
    }
}
