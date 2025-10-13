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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Карты", description = "API для управления банковскими картами")
public class CardController {
	
	private final CardService cardService;

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать новую карту", description = "Только для администраторов. Создает новую банковскую карту для пользователя.")
	public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardCreateRequest request) {
		return ResponseEntity.ok(cardService.createCard(request));
	}

	@GetMapping("/{id}")
    @Operation(summary = "Получить карту по ID", description = "Возвращает информацию о карте по её идентификатору")
	public ResponseEntity<CardResponse> getCard(@PathVariable Long id) {
		return ResponseEntity.ok(cardService.getCardById(id));
	}

	@GetMapping("/user/{userId}")
    @Operation(summary = "Получить карты пользователя", description = "Возвращает список карт пользователя с пагинацией")
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
    @Operation(summary = "Получить все карты", description = "Поиск всех карт с пагинацией")
	public ResponseEntity<Page<CardResponse>> getAllCards(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		return ResponseEntity.ok(cardService.getAllCards(pageable));
	}

	@PatchMapping("/{id}/status")
	@PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить статус карты", description = "Только для администраторов. Изменить статус карты")
	public ResponseEntity<CardResponse> updateCardStatus(@PathVariable Long id, @RequestParam CardStatus status) {

		return ResponseEntity.ok(cardService.updateCardStatus(id, status));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')") 
    @Operation(summary = "Изменить параметры карты", description = "Только для администраторов. Ищменить карту в соответствии с заданными параметрами")
	public ResponseEntity<CardResponse> updateCard(@PathVariable Long id,
			@Valid @RequestBody CardUpdateRequest request) {

		return ResponseEntity.ok(cardService.updateCard(id, request));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить карту", description = "Только для администраторов. Удаалить карту")
	public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
		cardService.deleteCard(id);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/user/{userId}/search")
    @Operation(summary = "Поиск карт с фильтрами", description = "Поиск карт пользователя с различными фильтрами: по статусу, балансу, имени владельца")
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
