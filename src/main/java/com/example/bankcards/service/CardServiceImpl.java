package com.example.bankcards.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bankcards.dto.request.CardCreateRequest;
import com.example.bankcards.dto.request.CardSearchRequest;
import com.example.bankcards.dto.request.CardUpdateRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.EncryptionUtil;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

	private final CardRepository cardRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final EncryptionUtil encryptionUtil;

	@Override
	@Transactional
	public CardResponse createCard(CardCreateRequest request) {

        if (request.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Дата истечения карты должна быть в будущем");
        }


        if (request.getInitialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Начальный баланс не может быть отрицательным");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));


        String cardNumber = generateCardNumber();
        String encryptedCardNumber = encryptionUtil.encrypt(cardNumber);
        String cardNumberHash = encryptionUtil.hash(cardNumber);

        Card card = new Card();
        card.setCardNumber(encryptedCardNumber);
        card.setCardNumberHash(cardNumberHash);
        card.setCardHolder(request.getCardHolder());
        card.setExpiryDate(request.getExpiryDate());
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(request.getInitialBalance());
        card.setUser(user);

        Card savedCard = cardRepository.save(card);
        return mapToCardResponse(savedCard, cardNumber);
    }

	@Override
	public CardResponse getCardById(Long id) {
		Card card = cardRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Card not found with id: " + id));
		String decryptedCardNumber = encryptionUtil.decrypt(card.getCardNumber());
		return mapToCardResponse(card, decryptedCardNumber);
	}

	@Override
	public Page<CardResponse> getUserCards(Long userId, Pageable pageable) {
		return cardRepository.findByUserId(userId, pageable).map(card -> {
			String decryptedCardNumber = encryptionUtil.decrypt(card.getCardNumber());
			return mapToCardResponse(card, decryptedCardNumber);
		});
	}

	@Override
	public Page<CardResponse> getAllCards(Pageable pageable) {
		return cardRepository.findAll(pageable).map(card -> {
			String decryptedCardNumber = encryptionUtil.decrypt(card.getCardNumber());
			return mapToCardResponse(card, decryptedCardNumber);
		});
	}

	@Override
	@Transactional
	public CardResponse updateCardStatus(Long id, CardStatus status) {
		Card card = cardRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Card not found with id: " + id));

		card.setStatus(status);
		Card updatedCard = cardRepository.save(card);

		String decryptedCardNumber = encryptionUtil.decrypt(updatedCard.getCardNumber());
		return mapToCardResponse(updatedCard, decryptedCardNumber);
	}

	@Override
	@Transactional
	public CardResponse updateCard(Long id, CardUpdateRequest request) {
		Card card = cardRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Card not found with id: " + id));

		if (request.getCardHolder() != null) {
			card.setCardHolder(request.getCardHolder());
		}
		if (request.getExpiryDate() != null) {
			card.setExpiryDate(request.getExpiryDate());
		}

		Card updatedCard = cardRepository.save(card);
		String decryptedCardNumber = encryptionUtil.decrypt(updatedCard.getCardNumber());
		return mapToCardResponse(updatedCard, decryptedCardNumber);
	}

	@Override
	@Transactional
	public void deleteCard(Long id) {
		Card card = cardRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Card not found with id: " + id));
		cardRepository.delete(card);
	}

	@Override
	public boolean isCardOwnedByUser(Long cardId, Long userId) {
		return cardRepository.existsByIdAndUserId(cardId, userId);
	}

	private String generateCardNumber() {
		StringBuilder cardNumber = new StringBuilder();
		for (int i = 0; i < 16; i++) {
			cardNumber.append((int) (Math.random() * 10));
		}
		return cardNumber.toString();
	}

	private CardResponse mapToCardResponse(Card card, String decryptedCardNumber) {
		CardResponse response = modelMapper.map(card, CardResponse.class);
		response.setMaskedCardNumber(maskCardNumber(decryptedCardNumber));
		response.setExpired(card.isExpired());
		response.setUserId(card.getUser().getId());
		return response;
	}

	private String maskCardNumber(String cardNumber) {
		if (cardNumber == null || cardNumber.length() < 4) {
			return cardNumber;
		}
		String lastFour = cardNumber.substring(cardNumber.length() - 4);
		return "**** **** **** " + lastFour;
	}
	
	@Override
	public Page<CardResponse> searchUserCards(Long userId, CardSearchRequest searchRequest, Pageable pageable) {
		Page<Card> cardsPage;

		if (searchRequest.getStatus() != null && searchRequest.getSearch() != null) {
			cardsPage = cardRepository.findByUserIdAndStatusAndCardHolderContaining(userId, searchRequest.getStatus(),
					searchRequest.getSearch(), pageable);
		} else if (searchRequest.getStatus() != null) {
			cardsPage = cardRepository.findByUserIdAndStatus(userId, searchRequest.getStatus(), pageable);
		} else if (searchRequest.getSearch() != null) {
			cardsPage = cardRepository.findByUserIdAndCardHolderContaining(userId, searchRequest.getSearch(), pageable);
		} else if (searchRequest.getMinBalance() != null) {
			cardsPage = cardRepository.findByUserIdAndBalanceGreaterThan(userId, searchRequest.getMinBalance(),
					pageable);
		} else if (searchRequest.getMaxBalance() != null) {
			cardsPage = cardRepository.findByUserIdAndBalanceLessThan(userId, searchRequest.getMaxBalance(), pageable);
		} else {
			cardsPage = cardRepository.findByUserId(userId, pageable);
		}
		return cardsPage.map(card -> {
			String decryptedCardNumber = encryptionUtil.decrypt(card.getCardNumber());
			return mapToCardResponse(card, decryptedCardNumber);
		});
	}

	
}
