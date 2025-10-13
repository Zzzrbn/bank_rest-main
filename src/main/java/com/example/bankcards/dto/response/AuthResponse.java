package com.example.bankcards.dto.response;

import lombok.Data;

@Data
public class AuthResponse {
	
	private String token;
	private String type = "Bearer";
	private UserResponse user;
}
