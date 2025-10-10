package com.example.bankcards.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CardUpdateRequest {
	
	@NotBlank(message = "Card holder is required")
    private String cardHolder;

    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;
}
