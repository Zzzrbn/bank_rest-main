package com.example.bankcards.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
	
	@NotNull(message = "Требуется идентификатор карты списания")
	private Long fromCardId;

	@NotNull(message = "Требуется идентификатор карты зачисления")
	private Long toCardId;

	@NotNull(message = "Начальный баланс обязателен")
	@DecimalMin(value = "0.01", message = "Баланс не может быть отрицательным")
	private BigDecimal amount;

	private String description;
}
