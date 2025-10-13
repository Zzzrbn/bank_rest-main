package com.example.bankcards.dto.response;

import java.time.LocalDateTime;

import com.example.bankcards.entity.enums.Role;

import lombok.Data;

@Data
public class UserResponse {
	
	private Long id;
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private Role role;
	private LocalDateTime createdAt;
}
