package com.example.bankcards.service;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.security.EncryptionUtil;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService{
	
	private final TransferRepository transferRepository;
    private final CardRepository cardRepository;
    private final ModelMapper modelMapper;
    private final EncryptionUtil encryptionUtil;

    @Override
    @Transactional
    public TransferResponse transferBetweenOwnCards(Long userId, TransferRequest request) {
        Card fromCard = cardRepository.findByIdAndUserId(request.getFromCardId(), userId)
                .orElseThrow(() -> new RuntimeException("Card not found or not owned by user"));

        Card toCard = cardRepository.findByIdAndUserId(request.getToCardId(), userId)
                .orElseThrow(() -> new RuntimeException("Card not found or not owned by user"));


        if (!fromCard.isActive()) {
            throw new RuntimeException("Source card is not active");
        }
        if (!toCard.isActive()) {
            throw new RuntimeException("Destination card is not active");
        }


        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Not enough money on source card");
        }


        fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
        toCard.setBalance(toCard.getBalance().add(request.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);


        Transfer transfer = new Transfer();
        transfer.setFromCard(fromCard);
        transfer.setToCard(toCard);
        transfer.setAmount(request.getAmount());
        transfer.setDescription(request.getDescription());

        Transfer savedTransfer = transferRepository.save(transfer);
        return mapToTransferResponse(savedTransfer);
    }

    @Override
    public Page<TransferResponse> getUserTransfers(Long userId, Pageable pageable) {
        return transferRepository.findByUserId(userId, pageable)
                .map(this::mapToTransferResponse);
    }

    @Override
    public Page<TransferResponse> getAllTransfers(Pageable pageable) {
        return transferRepository.findAll(pageable)
                .map(this::mapToTransferResponse);
    }

    @Override
    public TransferResponse getTransferById(Long id) {
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));
        return mapToTransferResponse(transfer);
    }

    private TransferResponse mapToTransferResponse(Transfer transfer) {
        TransferResponse response = modelMapper.map(transfer, TransferResponse.class);
        
        String fromCardNumber = encryptionUtil.decrypt(transfer.getFromCard().getCardNumber());
        String toCardNumber = encryptionUtil.decrypt(transfer.getToCard().getCardNumber());
        
        response.setFromCardMasked(maskCardNumber(fromCardNumber));
        response.setToCardMasked(maskCardNumber(toCardNumber));
        
        return response;
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return cardNumber;
        }
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }
}
