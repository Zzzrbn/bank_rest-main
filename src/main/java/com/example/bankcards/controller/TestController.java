package com.example.bankcards.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {
		
	@GetMapping("/public/hello")
	public Map<String, String> publicHello() {
		return Map.of("message", "Hello from public endpoint! No auth required.");
	}

	@GetMapping("/user/hello")
	public Map<String, Object> userHello(Authentication authentication) {
		return Map.of("message", "Hello user endpoint! Requires authentication.", "username", authentication.getName(),
				"role", authentication.getAuthorities().iterator().next().getAuthority());
	}

	@GetMapping("/admin/hello")
	public Map<String, Object> adminHello(Authentication authentication) {
		return Map.of("message", "Hello admin endpoint! Requires ADMIN role.", "username", authentication.getName(),
				"role", "ADMIN");
	}

}
