package com.example.bankcards.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на создание карты")
public class CardCreateRequest {
    
	@Schema(description = "Имя владельца карты", example = "Иван Иванов")
    @NotBlank(message = "Имя владельца карты обязательно")
    @Size(min = 2, max = 50, message = "Имя владельца должно быть от 2 до 50 символов")
    private String cardHolder;

    @Schema(description = "ID пользователя", example = "1")
    @NotNull(message = "ID пользователя обязателен")
    private Long userId;

    @Schema(description = "Дата истечения срока действия", example = "2026-12-31")
    @NotNull(message = "Дата истечения обязательна")
    @Future(message = "Дата истечения должна быть в будущем")
    private LocalDate expiryDate;

    @Schema(description = "Начальный баланс", example = "1000.00")
    @NotNull(message = "Начальный баланс обязателен")
    @DecimalMin(value = "0.0", message = "Баланс не может быть отрицательным")
    @Digits(integer = 10, fraction = 2, message = "Баланс должен быть в формате 12345.67")
    private BigDecimal initialBalance;
}
