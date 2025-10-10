package com.example.bankcards.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.TransferResponse;

public interface TransferService {
	
	TransferResponse transferBetweenOwnCards(Long userId, TransferRequest request);

	Page<TransferResponse> getUserTransfers(Long userId, Pageable pageable);

	Page<TransferResponse> getAllTransfers(Pageable pageable);

	TransferResponse getTransferById(Long id);
}
