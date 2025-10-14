package com.example.bankcards.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.example.bankcards.dto.request.CardCreateRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.EncryptionUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private EncryptionUtil encryptionUtil;

    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    void createCard_ShouldCreateCard_WhenValidRequest() {

        CardCreateRequest request = new CardCreateRequest();
        request.setCardHolder("Иван Иванов");
        request.setUserId(1L);
        request.setExpiryDate(LocalDate.now().plusYears(2));
        request.setInitialBalance(BigDecimal.valueOf(1000.50));

        User user = new User();
        user.setId(1L);
        user.setUsername("ivanov");
        
        CardResponse expectedResponse = new CardResponse();
        expectedResponse.setCardHolder("Иван Иванов");
        expectedResponse.setStatus(CardStatus.ACTIVE);
        expectedResponse.setBalance(BigDecimal.valueOf(1000.50));
        expectedResponse.setMaskedCardNumber("**** **** **** 1234");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(encryptionUtil.encrypt(any())).thenReturn("encrypted");
        when(encryptionUtil.hash(any())).thenReturn("hashed");
		when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
			Card savedCard = invocation.getArgument(0);
			savedCard.setId(1L);
			return savedCard;
		});
		when(modelMapper.map(any(Card.class), eq(CardResponse.class))).thenAnswer(invocation -> {
	        CardResponse response = new CardResponse();
	        response.setId(1L);
	        response.setCardHolder("Иван Иванов");
	        response.setStatus(CardStatus.ACTIVE);
	        response.setBalance(BigDecimal.valueOf(1000.50));
	        response.setMaskedCardNumber("**** **** **** 1234");
	        return response;
	    });
		
		CardResponse actualResponse = cardService.createCard(request);

		assertNotNull(actualResponse, "Результат не должен быть null");
		assertEquals("Иван Иванов", actualResponse.getCardHolder());
		assertEquals(CardStatus.ACTIVE, actualResponse.getStatus());
		assertEquals(BigDecimal.valueOf(1000.50), actualResponse.getBalance());
		
	    assertTrue(actualResponse.getMaskedCardNumber().startsWith("**** **** **** "),
	              "Номер карты должен начинаться с '**** **** **** '");
	    assertEquals(19, actualResponse.getMaskedCardNumber().length(),
	                "Длина замаскированного номера должна быть 19 символов");
        
		verify(userRepository, times(1)).findById(1L);
        verify(cardRepository, times(1)).save(any(Card.class));
        verify(encryptionUtil, times(1)).encrypt(anyString());
        verify(encryptionUtil, times(1)).hash(anyString());
        verify(modelMapper, times(1)).map(any(Card.class), eq(CardResponse.class));
        
        verifyNoMoreInteractions(userRepository, cardRepository, encryptionUtil, modelMapper);
    }

    @Test
    void getCardById_ShouldReturnCard_WhenCardExists() {

    	Long cardId = 1L;
        
		Card card = new Card();
		card.setId(cardId);
		card.setCardNumber("encrypted_card_number");
		card.setCardHolder("Петр Петров");
		card.setBalance(BigDecimal.valueOf(500.75));
		card.setExpiryDate(LocalDate.now().plusYears(1));

		User user = new User();
		user.setId(2L);
		card.setUser(user);

		CardResponse expectedResponse = new CardResponse();
		expectedResponse.setId(1L);
		expectedResponse.setCardHolder("Петр Петров");
		expectedResponse.setBalance(BigDecimal.valueOf(500.75));
		expectedResponse.setMaskedCardNumber("**** **** **** 5678");

		when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
		when(encryptionUtil.decrypt("encrypted_card_number")).thenReturn("1234567812345678");
		when(modelMapper.map(any(Card.class), eq(CardResponse.class))).thenReturn(expectedResponse);

		CardResponse actualResponse = cardService.getCardById(cardId);

		assertNotNull(actualResponse, "Карта должна быть найдена");
		assertEquals(1L, actualResponse.getId(), "ID карты должен быть 1");
		assertEquals("Петр Петров", actualResponse.getCardHolder(), "Имя владельца должно совпадать");
		assertEquals(BigDecimal.valueOf(500.75), actualResponse.getBalance(), "Баланс должен быть 500.75");
		assertEquals("**** **** **** 5678", actualResponse.getMaskedCardNumber(),
				"Номер карты должен быть замаскирован");

		verify(cardRepository, times(1)).findById(cardId);
		verify(encryptionUtil, times(1)).decrypt("encrypted_card_number");
		verify(modelMapper, times(1)).map(any(Card.class), eq(CardResponse.class));
    }
    
    @Test
    void getCardById_ShouldThrowException_WhenCardNotFound() {

		Long cardId = 999L;
		when(cardRepository.findById(cardId)).thenReturn(Optional.empty());
		Exception exception = assertThrows(RuntimeException.class, () -> {
			cardService.getCardById(cardId);
		});

		assertTrue(exception.getMessage().contains("не найден") || exception.getMessage().contains("not found"),
				"Сообщение об ошибке должно содержать информацию о ненайденной карте");

		verify(cardRepository, times(1)).findById(cardId);
		verify(encryptionUtil, never()).decrypt(anyString());
    }
    
}
