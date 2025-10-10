package com.example.bankcards.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bankcards.entity.Card;

public interface CardRepository extends JpaRepository<Card, Long>{
	
    Page<Card> findByUserId(Long userId, Pageable pageable);
    
    Optional<Card> findByCardNumberHash(String cardNumberHash);
    
    @Query("SELECT COUNT(c) > 0 FROM Card c WHERE c.id = :cardId AND c.user.id = :userId")
    boolean existsByIdAndUserId(@Param("cardId") Long cardId, @Param("userId") Long userId);
    
    @Query("SELECT c FROM Card c WHERE c.id = :cardId AND c.user.id = :userId")
    Optional<Card> findByIdAndUserId(@Param("cardId") Long cardId, @Param("userId") Long userId);
}
