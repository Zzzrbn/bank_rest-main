package com.example.bankcards.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.security.EncryptionUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

	@Mock
    private TransferRepository transferRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private EncryptionUtil encryptionUtil;

    @InjectMocks
    private TransferServiceImpl transferService;

    @Test
    void transferBetweenOwnCards_ShouldTransfer_WhenValidRequest() {

        Long userId = 1L;
        TransferRequest request = new TransferRequest();
        request.setFromCardId(1L);
        request.setToCardId(2L);
        request.setAmount(BigDecimal.valueOf(100));
        request.setDescription("Test transfer");

        User user = new User();
        user.setId(userId);

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setUser(user);
        fromCard.setBalance(BigDecimal.valueOf(500));
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setExpiryDate(LocalDate.now().plusYears(1));

		Card toCard = new Card();
		toCard.setId(2L);
		toCard.setUser(user);
		toCard.setBalance(BigDecimal.valueOf(200));
		toCard.setStatus(CardStatus.ACTIVE);
		toCard.setExpiryDate(LocalDate.now().plusYears(1));
		
	    TransferResponse expectedResponse = new TransferResponse();
	    expectedResponse.setFromCardMasked("**** **** **** 1234");
	    expectedResponse.setToCardMasked("**** **** **** 5678");
	    expectedResponse.setAmount(BigDecimal.valueOf(100));

		when(modelMapper.map(any(), eq(TransferResponse.class))).thenReturn(expectedResponse);
		when(cardRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(fromCard));
		when(cardRepository.findByIdAndUserId(2L, userId)).thenReturn(Optional.of(toCard));
		when(transferRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		TransferResponse result = transferService.transferBetweenOwnCards(userId, request);

		assertNotNull(result);
		verify(transferRepository, times(1)).save(any());
    }
}
