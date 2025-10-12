package com.example.bankcards.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.bankcards.dto.request.CardCreateRequest;
import com.example.bankcards.dto.request.CardSearchRequest;
import com.example.bankcards.dto.request.CardUpdateRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.CardService;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
	
	private final CardService cardService;

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardCreateRequest request) {
		return ResponseEntity.ok(cardService.createCard(request));
	}

	@GetMapping("/{id}")
	public ResponseEntity<CardResponse> getCard(@PathVariable Long id) {
		return ResponseEntity.ok(cardService.getCardById(id));
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<Page<CardResponse>> getUserCards(@PathVariable Long userId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "desc") String direction) {

		Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		return ResponseEntity.ok(cardService.getUserCards(userId, pageable));
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Page<CardResponse>> getAllCards(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		return ResponseEntity.ok(cardService.getAllCards(pageable));
	}

	@PatchMapping("/{id}/status")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CardResponse> updateCardStatus(@PathVariable Long id, @RequestParam CardStatus status) {

		return ResponseEntity.ok(cardService.updateCardStatus(id, status));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CardResponse> updateCard(@PathVariable Long id,
			@Valid @RequestBody CardUpdateRequest request) {

		return ResponseEntity.ok(cardService.updateCard(id, request));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
		cardService.deleteCard(id);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/user/{userId}/search")
	public ResponseEntity<Page<CardResponse>> searchUserCards(@PathVariable Long userId,
			@RequestBody CardSearchRequest searchRequest, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Sort sort = searchRequest.getDirection().equals("desc") ? Sort.by(searchRequest.getSortBy()).descending()
				: Sort.by(searchRequest.getSortBy()).ascending();

		Pageable pageable = PageRequest.of(page, size, sort);

		Page<CardResponse> cards = cardService.searchUserCards(userId, searchRequest, pageable);
		return ResponseEntity.ok(cards);
	}

	@GetMapping("/user/{userId}/filter")
	public ResponseEntity<Page<CardResponse>> filterUserCards(@PathVariable Long userId,
			@RequestParam(required = false) String status, @RequestParam(required = false) Double minBalance,
			@RequestParam(required = false) Double maxBalance, @RequestParam(required = false) String search,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		CardSearchRequest searchRequest = new CardSearchRequest();

		if (status != null) {
			try {
				searchRequest.setStatus(CardStatus.valueOf(status.toUpperCase()));
			} catch (IllegalArgumentException e) {
				// Если статус невалидный - игнорируем
			}
		}

		searchRequest.setMinBalance(minBalance);
		searchRequest.setMaxBalance(maxBalance);
		searchRequest.setSearch(search);

		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<CardResponse> cards = cardService.searchUserCards(userId, searchRequest, pageable);
		return ResponseEntity.ok(cards);
	}
}
