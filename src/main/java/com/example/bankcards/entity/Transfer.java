package com.example.bankcards.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "bank-rest-transfers")
@Getter
@Setter
@NoArgsConstructor
public class Transfer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "from_card_id", nullable = false)
	private Card fromCard;

	@ManyToOne
	@JoinColumn(name = "to_card_id", nullable = false)
	private Card toCard;

	@Column(nullable = false)
	private BigDecimal amount;

	private String description;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	@Override
	public int hashCode() {
		return Objects.hash(amount, createdAt, description, fromCard, toCard);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transfer other = (Transfer) obj;
		return Objects.equals(amount, other.amount) && Objects.equals(createdAt, other.createdAt)
				&& Objects.equals(description, other.description) && Objects.equals(fromCard, other.fromCard)
				&& Objects.equals(toCard, other.toCard);
	}

	
}
