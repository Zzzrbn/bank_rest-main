package com.example.bankcards.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferResponse {
	private Long id;
	private String fromCardMasked;
	private String toCardMasked;
	private BigDecimal amount;
	private String description;
	private LocalDateTime createdAt;
}
