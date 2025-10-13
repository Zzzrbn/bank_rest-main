package com.example.bankcards.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.EncryptionUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    	
	private final UserRepository userRepository;
	private final CardRepository cardRepository;
	private final PasswordEncoder passwordEncoder;
	private final EncryptionUtil encryptionUtil;

	@Override
	public void run(String... args) throws Exception {
		log.info("=== Система управления банковскими картами ===");
		createDefaultUsers();
		createTestCards();
        log.info("Система успешно запущена и готова к работе!");
        log.info("Администратор: admin / admin123");
        log.info("Обычный пользователь: user / user123");
        log.info("Документация API: http://localhost:8080/swagger-ui/index.html");
        log.info("Health check: http://localhost:8080/api/health");
		log.info("===================================");
	}

	private void createDefaultUsers() {
		if (!userRepository.existsByUsername("admin")) {
			User admin = new User("admin", passwordEncoder.encode("admin123"), "admin@bank.com", "System",
					"Administrator", Role.ROLE_ADMIN);
			userRepository.save(admin);
			log.info("Создан администратор: admin");
		}
		else {
            log.info("Администратор уже существует: admin");
        }

		if (!userRepository.existsByUsername("user")) {
			User user = new User("user", passwordEncoder.encode("user123"), "user@bank.com", "John", "Doe",
					Role.ROLE_USER);
			userRepository.save(user);
			log.info("Создан пользователь: user");
		}
		else {
            log.info("Пользователь уже существует: user");
        }
	}

	private void createTestCards() {
		Optional<User> userOpt = userRepository.findByUsername("user");
		if (userOpt.isPresent() && cardRepository.count() == 0) {
			User user = userOpt.get();

			Card card1 = new Card();
			card1.setCardNumber(encryptionUtil.encrypt("1111222233334444"));
			card1.setCardNumberHash(encryptionUtil.hash("1111222233334444"));
			card1.setCardHolder("John Doe");
			card1.setExpiryDate(LocalDate.now().plusYears(2));
			card1.setStatus(CardStatus.ACTIVE);
			card1.setBalance(new BigDecimal("1000.00"));
			card1.setUser(user);
			cardRepository.save(card1);
			log.info("Создана тестовая карта 1 для пользователя user");

			Card card2 = new Card();
			card2.setCardNumber(encryptionUtil.encrypt("5555666677778888"));
			card2.setCardNumberHash(encryptionUtil.hash("5555666677778888"));
			card2.setCardHolder("John Doe");
			card2.setExpiryDate(LocalDate.now().plusYears(3));
			card2.setStatus(CardStatus.ACTIVE);
			card2.setBalance(new BigDecimal("2500.50"));
			card2.setUser(user);
			cardRepository.save(card2);
			log.info("Создана тестовая карта 2 для пользователя user");			
		} else {
            log.info("Тестовые карты уже созданы или пользователь не найден");
        }
		
	}
}
