package com.example.bankcards.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bankcards.dto.request.LoginRequest;
import com.example.bankcards.dto.request.RegisterRequest;
import com.example.bankcards.dto.response.AuthResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.CustomUserDetailsService;
import com.example.bankcards.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "API для регистрации и входа пользователей")
public class AuthController {

	private final CustomUserDetailsService userDetailsService;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя", description = "Создает нового пользователя с ролью USER")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		log.info("Регистрация пользователя: {}", request.getUsername());

		if (userRepository.existsByUsername(request.getUsername())) {
			log.warn("Регистрация невозможна: имя пользователя {} уже занято", request.getUsername());
			throw new RuntimeException("Имя пользователя уже занято");
		}
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email уже используется");
		}

		if (request.getPassword().length() < 6) {
			throw new RuntimeException("Пароль должен быть не менее 6 символов");
		}

		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setEmail(request.getEmail());
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setRole(Role.ROLE_USER);
		user.setCreatedAt(LocalDateTime.now());
		user.setUpdatedAt(LocalDateTime.now());

		User savedUser = userRepository.save(user);

		UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
		String jwtToken = jwtService.generateToken(userDetails);

		AuthResponse authResponse = new AuthResponse();
		authResponse.setToken(jwtToken);
		authResponse.setUser(mapToUserResponse(savedUser));
		log.info("Успешная регистрация пользователя: {}", request.getUsername());
		return ResponseEntity.ok(authResponse);
	}

	@PostMapping("/login")
    @Operation(summary = "Аутентификация пользователя", description = "Вход в систему и получение JWT токена")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		log.info("Авторизация пользователя: {}", request.getUsername());
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String jwtToken = jwtService.generateToken(userDetails);

		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		AuthResponse authResponse = new AuthResponse();
		authResponse.setToken(jwtToken);
		authResponse.setUser(mapToUserResponse(user));
		log.info("Успешная авторизация пользователя: {}", request.getUsername());
		return ResponseEntity.ok(authResponse);
	}

	private UserResponse mapToUserResponse(User user) {
		UserResponse response = new UserResponse();
		response.setId(user.getId());
		response.setUsername(user.getUsername());
		response.setEmail(user.getEmail());
		response.setFirstName(user.getFirstName());
		response.setLastName(user.getLastName());
		response.setRole(user.getRole());
		response.setCreatedAt(user.getCreatedAt());
		return response;
	}
}
