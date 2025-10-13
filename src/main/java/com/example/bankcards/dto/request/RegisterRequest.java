package com.example.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на регистрацию пользователя")
public class RegisterRequest {

	@Schema(description = "Имя пользователя", example = "ivanov")
	@NotBlank(message = "Имя пользователя обязательно")
	@Size(min = 3, max = 20, message = "Имя пользователя должно быть от 3 до 20 символов")
	private String username;

	@Schema(description = "Пароль", example = "Password123")
	@NotBlank(message = "Пароль обязателен")
	@Size(min = 6, message = "Пароль должен быть не менее 6 символов")
	private String password;

	@Schema(description = "Email адрес", example = "ivanov@mail.ru")
	@Email(message = "Email должен быть валидным")
	@NotBlank(message = "Email обязателен")
	private String email;

	@Schema(description = "Имя", example = "Иван")
	@NotBlank(message = "Имя обязательно")
	private String firstName;

	@Schema(description = "Фамилия", example = "Иванов")
	@NotBlank(message = "Фамилия обязательна")
	private String lastName;
}
