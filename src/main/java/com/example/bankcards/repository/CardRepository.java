
package com.example.bankcards.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;

public interface CardRepository extends JpaRepository<Card, Long>{
	
	Page<Card> findByUserId(Long userId, Pageable pageable);

	Optional<Card> findByCardNumberHash(String cardNumberHash);

	@Query("SELECT COUNT(c) > 0 FROM Card c WHERE c.id = :cardId AND c.user.id = :userId")
	boolean existsByIdAndUserId(@Param("cardId") Long cardId, @Param("userId") Long userId);

	@Query("SELECT c FROM Card c WHERE c.id = :cardId AND c.user.id = :userId")
	Optional<Card> findByIdAndUserId(@Param("cardId") Long cardId, @Param("userId") Long userId);

	Page<Card> findByUserIdAndStatus(Long userId, CardStatus status, Pageable pageable);

	@Query("SELECT c FROM Card c WHERE c.user.id = :userId AND c.balance > :minBalance")
	Page<Card> findByUserIdAndBalanceGreaterThan(@Param("userId") Long userId, @Param("minBalance") Double minBalance,
			Pageable pageable);

	@Query("SELECT c FROM Card c WHERE c.user.id = :userId AND c.balance < :maxBalance")
	Page<Card> findByUserIdAndBalanceLessThan(@Param("userId") Long userId, @Param("maxBalance") Double maxBalance,
			Pageable pageable);

	@Query("SELECT c FROM Card c WHERE c.user.id = :userId AND LOWER(c.cardHolder) LIKE LOWER(CONCAT('%', :search, '%'))")
	Page<Card> findByUserIdAndCardHolderContaining(@Param("userId") Long userId, @Param("search") String search,
			Pageable pageable);

	Page<Card> findByStatus(CardStatus status, Pageable pageable);

	@Query("SELECT c FROM Card c WHERE c.user.id = :userId AND c.status = :status AND LOWER(c.cardHolder) LIKE LOWER(CONCAT('%', :search, '%'))")
	Page<Card> findByUserIdAndStatusAndCardHolderContaining(@Param("userId") Long userId,
			@Param("status") CardStatus status, @Param("search") String search, Pageable pageable);

}
