package com.example.bankcards.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import com.example.bankcards.entity.enums.CardStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bank-rest-cards")
@Getter
@Setter
@NoArgsConstructor
public class Card {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "card_number", nullable = false)
	private String cardNumber;

	@Column(name = "card_number_hash", nullable = false, unique = true)
	private String cardNumberHash;

	@Column(name = "card_holder", nullable = false)
	private String cardHolder;

	@Column(name = "expiry_date", nullable = false)
	private LocalDate expiryDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CardStatus status;

	@Column(nullable = false)
	private BigDecimal balance;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public boolean isActive() {
		return status == CardStatus.ACTIVE && !isExpired();
	}

	public boolean isExpired() {
		return expiryDate.isBefore(LocalDate.now());
	}

	@Override
	public int hashCode() {
		return Objects.hash(balance, cardHolder, cardNumber, cardNumberHash, createdAt, expiryDate, status, updatedAt,
				user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Card other = (Card) obj;
		return Objects.equals(balance, other.balance) && Objects.equals(cardHolder, other.cardHolder)
				&& Objects.equals(cardNumber, other.cardNumber) && Objects.equals(cardNumberHash, other.cardNumberHash)
				&& Objects.equals(createdAt, other.createdAt) && Objects.equals(expiryDate, other.expiryDate)
				&& status == other.status && Objects.equals(updatedAt, other.updatedAt)
				&& Objects.equals(user, other.user);
	}
	
}
