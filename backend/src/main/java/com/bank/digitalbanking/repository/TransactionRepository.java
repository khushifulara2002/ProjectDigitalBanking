package com.bank.digitalbanking.repository;

import com.bank.digitalbanking.entity.Transaction;
import com.bank.digitalbanking.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionReference(String transactionReference);

    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);

    // Paginated search showing BOTH outgoing (t.account) and incoming (t.relatedAccount) transfers
    @Query("SELECT t FROM Transaction t WHERE (t.account.id = :accountId OR t.relatedAccount.id = :accountId) " +
            "AND (:type IS NULL OR t.type = :type) " +
            "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR t.createdAt <= :endDate)")
    Page<Transaction> filterTransactions(
            @Param("accountId") Long accountId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}