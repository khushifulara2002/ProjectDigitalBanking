package com.bank.digitalbanking.service;

import com.bank.digitalbanking.dto.request.DepositRequest;
import com.bank.digitalbanking.dto.request.TransferRequest;
import com.bank.digitalbanking.dto.request.WithdrawRequest;
import com.bank.digitalbanking.dto.response.PagedResponse;
import com.bank.digitalbanking.dto.response.TransactionResponse;
import com.bank.digitalbanking.entity.*;
import com.bank.digitalbanking.exception.AccountBlockedException;
import com.bank.digitalbanking.exception.BadRequestException;
import com.bank.digitalbanking.exception.InsufficientBalanceException;
import com.bank.digitalbanking.exception.ResourceNotFoundException;
import com.bank.digitalbanking.exception.UnauthorizedException;
import com.bank.digitalbanking.repository.AccountRepository;
import com.bank.digitalbanking.repository.TransactionRepository;
import com.bank.digitalbanking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    // 1. DEPOSIT MONEY
    @Override
    @Transactional
    public TransactionResponse deposit(String userEmail, DepositRequest request) {
        Account account = getAccount(request.getAccountNumber());
        validateAccountIsActive(account);

        // Update Balance
        account.setBalance(account.getBalance().add(request.getAmount()));

        // Record Transaction Ledger
        Transaction transaction = Transaction.builder()
                .transactionReference(generateReferenceCode())
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .status(TransactionStatus.SUCCESS)
                .description(request.getDescription() != null ? request.getDescription() : "Cash Deposit")
                .build();

        Transaction savedTxn = transactionRepository.save(transaction);
        return mapToTransactionResponse(savedTxn);
    }

    // 2. WITHDRAW MONEY
    @Override
    @Transactional
    public TransactionResponse withdraw(String userEmail, WithdrawRequest request) {
        Account account = getAccount(request.getAccountNumber());
        User user = getUser(userEmail);

        validateAccountOwnership(account, user);
        validateAccountIsActive(account);

        // Check Balance
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance in account " + account.getAccountNumber() +
                            ". Available: ₹" + account.getBalance());
        }

        // Deduct Balance
        account.setBalance(account.getBalance().subtract(request.getAmount()));

        // Record Transaction Ledger
        Transaction transaction = Transaction.builder()
                .transactionReference(generateReferenceCode())
                .account(account)
                .type(TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .status(TransactionStatus.SUCCESS)
                .description(request.getDescription() != null ? request.getDescription() : "Cash Withdrawal")
                .build();

        Transaction savedTxn = transactionRepository.save(transaction);
        return mapToTransactionResponse(savedTxn);
    }

    // 3. TRANSFER FUNDS (THE CORE ATOMIC OPERATION)
    @Override
    @Transactional
    public TransactionResponse transfer(String userEmail, TransferRequest request) {
        // Prevent Self-Transfer
        if (request.getSourceAccountNumber().equals(request.getTargetAccountNumber())) {
            throw new BadRequestException("Cannot transfer money to the same account");
        }

        Account sourceAccount = getAccount(request.getSourceAccountNumber());
        Account targetAccount = getAccount(request.getTargetAccountNumber());
        User user = getUser(userEmail);

        // Validations
        validateAccountOwnership(sourceAccount, user);
        validateAccountIsActive(sourceAccount);
        validateAccountIsActive(targetAccount);

        BigDecimal amount = request.getAmount();
        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Transfer failed: Insufficient balance in source account " + sourceAccount.getAccountNumber());
        }

        // Debit Source & Credit Target
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        targetAccount.setBalance(targetAccount.getBalance().add(amount));

        // Create Immutable Transaction Record
        Transaction transaction = Transaction.builder()
                .transactionReference(generateReferenceCode())
                .account(sourceAccount)
                .relatedAccount(targetAccount)
                .type(TransactionType.TRANSFER)
                .amount(amount)
                .status(TransactionStatus.SUCCESS)
                .description(request.getDescription() != null ? request.getDescription() : "Fund Transfer")
                .build();

        Transaction savedTxn = transactionRepository.save(transaction);
        return mapToTransactionResponse(savedTxn);
    }

    // 4. FETCH PAGINATED ACCOUNT TRANSACTIONS
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TransactionResponse> getAccountTransactions(
            String userEmail, String accountNumber, int page, int size) {

        Account account = getAccount(accountNumber);
        User user = getUser(userEmail);
        validateAccountOwnership(account, user);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Transaction> transactionPage = transactionRepository.findByAccountId(account.getId(), pageable);

        return PagedResponse.<TransactionResponse>builder()
                .content(transactionPage.getContent().stream().map(this::mapToTransactionResponse).toList())
                .pageNumber(transactionPage.getNumber())
                .pageSize(transactionPage.getSize())
                .totalElements(transactionPage.getTotalElements())
                .totalPages(transactionPage.getTotalPages())
                .last(transactionPage.isLast())
                .build();
    }

    // Private Helper Methods
    private Account getAccount(String accNum) {
        return accountRepository.findByAccountNumber(accNum)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + accNum));
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private void validateAccountIsActive(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountBlockedException("Account " + account.getAccountNumber() + " is currently " + account.getStatus());
        }
    }

    private void validateAccountOwnership(Account account, User user) {
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        if (!account.getUser().getId().equals(user.getId()) && !isAdmin) {
            throw new UnauthorizedException("Access denied: You do not own account " + account.getAccountNumber());
        }
    }

    private String generateReferenceCode() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private TransactionResponse mapToTransactionResponse(Transaction txn) {
        String sourceOwner = (txn.getAccount() != null && txn.getAccount().getUser() != null)
                ? txn.getAccount().getUser().getFirstName() + " " + txn.getAccount().getUser().getLastName()
                : "Unknown";

        String targetOwner = (txn.getRelatedAccount() != null && txn.getRelatedAccount().getUser() != null)
                ? txn.getRelatedAccount().getUser().getFirstName() + " " + txn.getRelatedAccount().getUser().getLastName()
                : null;

        return TransactionResponse.builder()
                .id(txn.getId())
                .transactionReference(txn.getTransactionReference())
                .sourceAccountNumber(txn.getAccount().getAccountNumber())
                .targetAccountNumber(txn.getRelatedAccount() != null ? txn.getRelatedAccount().getAccountNumber() : null)
                .sourceAccountOwnerName(sourceOwner)
                .targetAccountOwnerName(targetOwner)
                .type(txn.getType())
                .amount(txn.getAmount())
                .status(txn.getStatus())
                .description(txn.getDescription())
                .createdAt(txn.getCreatedAt())
                .build();
    }

    // Add this implementation method inside TransactionServiceImpl class:
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TransactionResponse> getFilteredTransactions(
            String userEmail,
            String accountNumber,
            TransactionType type,
            java.time.LocalDateTime startDate,
            java.time.LocalDateTime endDate,
            int page,
            int size) {

        Account account = getAccount(accountNumber);
        User user = getUser(userEmail);
        validateAccountOwnership(account, user);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Transaction> transactionPage = transactionRepository.filterTransactions(
                account.getId(), type, startDate, endDate, pageable);

        return PagedResponse.<TransactionResponse>builder()
                .content(transactionPage.getContent().stream().map(this::mapToTransactionResponse).toList())
                .pageNumber(transactionPage.getNumber())
                .pageSize(transactionPage.getSize())
                .totalElements(transactionPage.getTotalElements())
                .totalPages(transactionPage.getTotalPages())
                .last(transactionPage.isLast())
                .build();
    }
}