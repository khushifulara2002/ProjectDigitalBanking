package com.bank.digitalbanking.service;

import com.bank.digitalbanking.dto.request.CreateAccountRequest;
import com.bank.digitalbanking.dto.response.AccountResponse;
import com.bank.digitalbanking.entity.Account;
import com.bank.digitalbanking.entity.AccountStatus;
import com.bank.digitalbanking.entity.Role;
import com.bank.digitalbanking.entity.User;
import com.bank.digitalbanking.exception.BadRequestException;
import com.bank.digitalbanking.exception.ResourceNotFoundException;
import com.bank.digitalbanking.exception.UnauthorizedException;
import com.bank.digitalbanking.repository.AccountRepository;
import com.bank.digitalbanking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    public AccountResponse createAccount(String userEmail, CreateAccountRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        // 1. Generate Unique Account Number
        String accountNumber = generateUniqueAccountNumber();

        // 2. Build Account Entity
        Account account = Account.builder()
                .user(user)
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .balance(request.getInitialDeposit())
                .status(AccountStatus.ACTIVE)
                .build();

        // 3. Save to MySQL
        Account savedAccount = accountRepository.save(account);

        return mapToAccountResponse(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getUserAccounts(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        return accountRepository.findByUserId(user.getId()).stream()
                .filter(acc -> acc.getStatus() != AccountStatus.CLOSED)
                .map(this::mapToAccountResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountByNumber(String userEmail, String accountNumber) {
        Account account = getAccountEntityByNumber(accountNumber);
        return mapToAccountResponse(account);
    }

    @Override
    @Transactional
    public void deleteAccount(String userEmail, String accountNumber) {
        Account account = getAccountEntityByNumber(accountNumber);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        if (!account.getUser().getId().equals(user.getId()) && !isAdmin) {
            throw new UnauthorizedException("Access denied: You do not own account " + accountNumber);
        }

        account.setStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccountEntityByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + accountNumber));
    }

    @Override
    @Transactional
    public AccountResponse updateAccountStatus(String accountNumber, AccountStatus status) {
        Account account = getAccountEntityByNumber(accountNumber);
        account.setStatus(status);
        // Dirty checking automatically updates MySQL on commit
        return mapToAccountResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::mapToAccountResponse)
                .toList();
    }

    // Helper: Account Number Generator (ACC + 10 digits)
    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            long number = 1000000000L + (long) (random.nextDouble() * 9000000000L);
            accountNumber = "ACC" + number;
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    // Helper: Entity -> DTO Mapper
    private AccountResponse mapToAccountResponse(Account account) {
        String fullName = account.getUser() != null ? account.getUser().getFirstName() + " " + account.getUser().getLastName() : "Unknown";
        String email = account.getUser() != null ? account.getUser().getEmail() : "";
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .status(account.getStatus())
                .blockReason(account.getBlockReason())
                .ownerName(fullName)
                .userEmail(email)
                .createdAt(account.getCreatedAt())
                .build();
    }
}