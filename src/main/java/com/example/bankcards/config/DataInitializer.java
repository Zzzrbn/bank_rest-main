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
import com.example.bankcards.service.UserService;
import com.example.bankcards.service.UserServiceImpl;

import jakarta.annotation.PostConstruct;
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
		log.info("=== BANK CARD MANAGEMENT SYSTEM ===");
		createDefaultUsers();
		createTestCards();
		log.info("Default users and test cards created successfully!");
		log.info("Admin: admin / admin123");
		log.info("User:  user / user123");
		log.info("===================================");
	}

	private void createDefaultUsers() {
		if (!userRepository.existsByUsername("admin")) {
			User admin = new User("admin", passwordEncoder.encode("admin123"), "admin@bank.com", "System",
					"Administrator", Role.ROLE_ADMIN);
			userRepository.save(admin);
		}

		if (!userRepository.existsByUsername("user")) {
			User user = new User("user", passwordEncoder.encode("user123"), "user@bank.com", "John", "Doe",
					Role.ROLE_USER);
			userRepository.save(user);
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

			Card card2 = new Card();
			card2.setCardNumber(encryptionUtil.encrypt("5555666677778888"));
			card2.setCardNumberHash(encryptionUtil.hash("5555666677778888"));
			card2.setCardHolder("John Doe");
			card2.setExpiryDate(LocalDate.now().plusYears(3));
			card2.setStatus(CardStatus.ACTIVE);
			card2.setBalance(new BigDecimal("2500.50"));
			card2.setUser(user);
			cardRepository.save(card2);

			log.info("Test cards created for user: user");
		}
	}
}
