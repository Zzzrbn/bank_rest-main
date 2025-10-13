package com.example.bankcards.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CardUpdateRequest {
	
	@NotBlank(message = "Имя владельца карты обязательно")
	private String cardHolder;

	@Future(message = "Дата истечения должна быть в будущем")
	private LocalDate expiryDate;
}
