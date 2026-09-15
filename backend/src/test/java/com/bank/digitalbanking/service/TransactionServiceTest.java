package com.bank.digitalbanking.service;

import com.bank.digitalbanking.dto.request.TransferRequest;
import com.bank.digitalbanking.dto.response.TransactionResponse;
import com.bank.digitalbanking.entity.*;
import com.bank.digitalbanking.exception.InsufficientBalanceException;
import com.bank.digitalbanking.repository.AccountRepository;
import com.bank.digitalbanking.repository.TransactionRepository;
import com.bank.digitalbanking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User testUser;
    private Account sourceAccount;
    private Account targetAccount;

    @BeforeEach
    void setUp() {
        Role customerRole = Role.builder().id(1L).name("ROLE_CUSTOMER").build();

        testUser = User.builder()
                .id(1L)
                .email("john.doe@gmail.com")
                .roles(Collections.singleton(customerRole))
                .build();

        sourceAccount = Account.builder()
                .id(101L)
                .user(testUser)
                .accountNumber("ACC101")
                .balance(new BigDecimal("50000.00"))
                .status(AccountStatus.ACTIVE)
                .build();

        targetAccount = Account.builder()
                .id(202L)
                .user(User.builder().id(2L).email("alice@gmail.com").build())
                .accountNumber("ACC202")
                .balance(new BigDecimal("10000.00"))
                .status(AccountStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should successfully transfer funds between accounts")
    void transfer_Success() {
        // Arrange
        TransferRequest request = TransferRequest.builder()
                .sourceAccountNumber("ACC101")
                .targetAccountNumber("ACC202")
                .amount(new BigDecimal("5000.00"))
                .description("Test Transfer")
                .build();

        when(accountRepository.findByAccountNumber("ACC101")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber("ACC202")).thenReturn(Optional.of(targetAccount));
        when(userRepository.findByEmail("john.doe@gmail.com")).thenReturn(Optional.of(testUser));

        Transaction mockTxn = Transaction.builder()
                .id(1L)
                .transactionReference("TXN-12345678")
                .account(sourceAccount)
                .relatedAccount(targetAccount)
                .type(TransactionType.TRANSFER)
                .amount(new BigDecimal("5000.00"))
                .status(TransactionStatus.SUCCESS)
                .build();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTxn);

        // Act
        TransactionResponse response = transactionService.transfer("john.doe@gmail.com", request);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("45000.00"), sourceAccount.getBalance()); // 50000 - 5000
        assertEquals(new BigDecimal("15000.00"), targetAccount.getBalance()); // 10000 + 5000
        assertEquals(TransactionType.TRANSFER, response.getType());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw InsufficientBalanceException when transfer amount exceeds balance")
    void transfer_InsufficientBalance_ThrowsException() {
        // Arrange
        TransferRequest request = TransferRequest.builder()
                .sourceAccountNumber("ACC101")
                .targetAccountNumber("ACC202")
                .amount(new BigDecimal("100000.00")) // Exceeds 50,000 balance
                .build();

        when(accountRepository.findByAccountNumber("ACC101")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber("ACC202")).thenReturn(Optional.of(targetAccount));
        when(userRepository.findByEmail("john.doe@gmail.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        InsufficientBalanceException exception = assertThrows(
                InsufficientBalanceException.class,
                () -> transactionService.transfer("john.doe@gmail.com", request)
        );

        assertTrue(exception.getMessage().contains("Insufficient balance"));
        assertEquals(new BigDecimal("50000.00"), sourceAccount.getBalance()); // Unchanged!
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}