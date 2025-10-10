package com.example.bankcards.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.bankcards.entity.Transfer;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
	
    @Query("SELECT t FROM Transfer t WHERE t.fromCard.user.id = :userId OR t.toCard.user.id = :userId")
    Page<Transfer> findByUserId(@Param("userId") Long userId, Pageable pageable);
    

    @Query("SELECT t FROM Transfer t WHERE t.fromCard.id = :cardId OR t.toCard.id = :cardId")
    Page<Transfer> findByCardId(@Param("cardId") Long cardId, Pageable pageable);
}
