package com.example.bankcards.controller;

import com.example.bankcards.dto.request.LoginRequest;
import com.example.bankcards.dto.request.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {
	
	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_ShouldReturnOk_WhenValidRequest() throws Exception {
        
		RegisterRequest request = new RegisterRequest();
		request.setUsername("testuser123");
		request.setPassword("password123");
		request.setEmail("test123@test.com");
		request.setFirstName("Test");
		request.setLastName("User");

		mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk());
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenInvalidCredentials() throws Exception {
        
    	LoginRequest request = new LoginRequest();
        request.setUsername("wronguser");
        request.setPassword("wrongpassword");

		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
	}
  
}
