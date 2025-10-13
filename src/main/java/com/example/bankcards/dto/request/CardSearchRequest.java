package com.example.bankcards.dto.request;

import com.example.bankcards.entity.enums.CardStatus;

import lombok.Data;

@Data
public class CardSearchRequest {
	
	private CardStatus status;
	private Double minBalance;
	private Double maxBalance;
	private String search;
	private String sortBy = "createdAt";
	private String direction = "desc";
}
