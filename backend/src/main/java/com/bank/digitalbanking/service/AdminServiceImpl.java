package com.bank.digitalbanking.service;

import com.bank.digitalbanking.dto.response.AccountResponse;
import com.bank.digitalbanking.dto.response.AuditLogResponse;
import com.bank.digitalbanking.dto.response.PagedResponse;
import com.bank.digitalbanking.dto.response.UserProfileResponse;
import com.bank.digitalbanking.entity.Account;
import com.bank.digitalbanking.entity.AccountStatus;
import com.bank.digitalbanking.entity.AuditLog;
import com.bank.digitalbanking.entity.User;
import com.bank.digitalbanking.exception.ResourceNotFoundException;
import com.bank.digitalbanking.repository.AccountRepository;
import com.bank.digitalbanking.repository.AuditLogRepository;
import com.bank.digitalbanking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::mapToAccountResponse)
                .toList();
    }

    @Override
    @Transactional
    public AccountResponse blockAccount(String accountNumber, String reason, String adminEmail, String ipAddress) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + accountNumber));

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        account.setStatus(AccountStatus.BLOCKED);
        account.setBlockReason(reason != null && !reason.isBlank() ? reason : "Regulatory Freeze / Security Risk");

        // Record Audit Trail
        AuditLog auditLog = AuditLog.builder()
                .performedByUser(admin)
                .action("BLOCK_ACCOUNT")
                .details("Account " + accountNumber + " blocked. Reason: " + account.getBlockReason())
                .ipAddress(ipAddress)
                .build();
        auditLogRepository.save(auditLog);

        return mapToAccountResponse(account);
    }

    @Override
    @Transactional
    public AccountResponse unblockAccount(String accountNumber, String adminEmail, String ipAddress) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + accountNumber));

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        account.setStatus(AccountStatus.ACTIVE);
        account.setBlockReason(null);

        // Record Audit Trail
        AuditLog auditLog = AuditLog.builder()
                .performedByUser(admin)
                .action("UNBLOCK_ACCOUNT")
                .details("Account " + accountNumber + " unblocked by admin.")
                .ipAddress(ipAddress)
                .build();
        auditLogRepository.save(auditLog);

        return mapToAccountResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AuditLogResponse> getAuditLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AuditLog> auditPage = auditLogRepository.findAll(pageable);

        return PagedResponse.<AuditLogResponse>builder()
                .content(auditPage.getContent().stream().map(this::mapToAuditLogResponse).toList())
                .pageNumber(auditPage.getNumber())
                .pageSize(auditPage.getSize())
                .totalElements(auditPage.getTotalElements())
                .totalPages(auditPage.getTotalPages())
                .last(auditPage.isLast())
                .build();
    }

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

    private AuditLogResponse mapToAuditLogResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .performedByEmail(log.getPerformedByUser() != null ? log.getPerformedByUser().getEmail() : "SYSTEM")
                .action(log.getAction())
                .details(log.getDetails())
                .ipAddress(log.getIpAddress())
                .createdAt(log.getCreatedAt())
                .build();
    }
}