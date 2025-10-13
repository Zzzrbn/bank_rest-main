package com.example.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Запрос на аутентификацию")
public class LoginRequest {

	@Schema(description = "Имя пользователя", example = "ivanov")
	@NotBlank(message = "Имя пользователя обязательно")
	private String username;

	@Schema(description = "Пароль", example = "myPassword123")
	@NotBlank(message = "Пароль обязателен")
	private String password;
}
