package com.example.bankcards.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.bankcards.dto.request.CardCreateRequest;
import com.example.bankcards.dto.request.CardSearchRequest;
import com.example.bankcards.dto.request.CardUpdateRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.enums.CardStatus;

public interface CardService {

	CardResponse createCard(CardCreateRequest request);

	CardResponse getCardById(Long id);

	Page<CardResponse> getUserCards(Long userId, Pageable pageable);

	Page<CardResponse> getAllCards(Pageable pageable);

	CardResponse updateCardStatus(Long id, CardStatus status);

	CardResponse updateCard(Long id, CardUpdateRequest request);

	void deleteCard(Long id);

	boolean isCardOwnedByUser(Long cardId, Long userId);

	Page<CardResponse> searchUserCards(Long userId, CardSearchRequest searchRequest, Pageable pageable);
}
