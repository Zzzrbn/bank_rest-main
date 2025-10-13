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

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.service.TransferService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Tag(name = "Переводы", description = "API для управления денежными переводами")
public class TransferController {
	
	private final TransferService transferService;

	@PostMapping
	@Operation(summary = "Создать перевод между картами", description = "Перевод денег между картами одного пользователя")
	public ResponseEntity<TransferResponse> transferBetweenOwnCards(@RequestParam Long userId,
			@Valid @RequestBody TransferRequest request) {
		return ResponseEntity.ok(transferService.transferBetweenOwnCards(userId, request));
	}

	@GetMapping("/user/{userId}")
	@Operation(summary = "История переводов пользователя", description = "Возвращает историю переводов пользователя с пагинацией")
	public ResponseEntity<Page<TransferResponse>> getUserTransfers(
			@PathVariable Long userId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "desc") String direction) {

		Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		return ResponseEntity.ok(transferService.getUserTransfers(userId, pageable));
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Page<TransferResponse>> getAllTransfers(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		return ResponseEntity.ok(transferService.getAllTransfers(pageable));
	}

	@GetMapping("/{id}")
	public ResponseEntity<TransferResponse> getTransfer(@PathVariable Long id) {
		return ResponseEntity.ok(transferService.getTransferById(id));
	}
}
