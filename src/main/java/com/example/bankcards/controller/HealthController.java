package com.example.bankcards.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {
	
	@GetMapping("/health")
	public Map<String, String> healthCheck() {
		Map<String, String> status = new HashMap<>();
		status.put("status", "OK");
		status.put("service", "Bank Card API");
		status.put("timestamp", java.time.LocalDateTime.now().toString());
		return status;
	}

	@GetMapping("/info")
	public Map<String, String> serviceInfo() {
		Map<String, String> info = new HashMap<>();
		info.put("name", "Bank Card API");
		info.put("version", "1.0.0");
		info.put("description", "Тестовое приложение для управления банковскими картами");
		info.put("developer", "Zzzrbn");
		return info;
	}
}
