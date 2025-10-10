package com.example.bankcards.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.bankcards.entity.enums.CardStatus;

import lombok.Data;

@Data
public class CardResponse {
    private Long id;
    private String maskedCardNumber;
    private String cardHolder;
    private LocalDate expiryDate;
    private CardStatus status;
    private BigDecimal balance;
    private Long userId;
    private LocalDateTime createdAt;
    private boolean expired;
}
