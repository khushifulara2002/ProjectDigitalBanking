package com.bank.digitalbanking.repository;

import com.bank.digitalbanking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByUserId(Long userId);

    boolean existsByAccountNumber(String accountNumber);

    // Custom JPQL query to check if account belongs to a specific user
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Account a WHERE a.accountNumber = :accNum AND a.user.id = :userId")
    boolean isAccountOwnedByUser(@Param("accNum") String accountNumber, @Param("userId") Long userId);
}