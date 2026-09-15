package com.bank.digitalbanking.controller;

import com.bank.digitalbanking.dto.request.DepositRequest;
import com.bank.digitalbanking.dto.request.TransferRequest;
import com.bank.digitalbanking.dto.request.WithdrawRequest;
import com.bank.digitalbanking.dto.response.ApiResponse;
import com.bank.digitalbanking.dto.response.PagedResponse;
import com.bank.digitalbanking.dto.response.TransactionResponse;
import com.bank.digitalbanking.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @Valid @RequestBody DepositRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();
        TransactionResponse response = transactionService.deposit(userEmail, request);
        return ResponseEntity.ok(ApiResponse.success("Deposit completed successfully", response));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(
            @Valid @RequestBody WithdrawRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();
        TransactionResponse response = transactionService.withdraw(userEmail, request);
        return ResponseEntity.ok(ApiResponse.success("Withdrawal completed successfully", response));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();
        TransactionResponse response = transactionService.transfer(userEmail, request);
        return ResponseEntity.ok(ApiResponse.success("Transfer completed successfully", response));
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<ApiResponse<PagedResponse<TransactionResponse>>> getAccountTransactions(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        String userEmail = authentication.getName();
        PagedResponse<TransactionResponse> response = transactionService.getAccountTransactions(
                userEmail, accountNumber, page, size);

        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", response));
    }


    // Add this endpoint inside TransactionController class:
    @GetMapping("/statement/{accountNumber}")
    public ResponseEntity<ApiResponse<PagedResponse<TransactionResponse>>> getAccountStatement(
            @PathVariable String accountNumber,
            @RequestParam(required = false) com.bank.digitalbanking.entity.TransactionType type,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        String userEmail = authentication.getName();
        PagedResponse<TransactionResponse> response = transactionService.getFilteredTransactions(
                userEmail, accountNumber, type, startDate, endDate, page, size);

        return ResponseEntity.ok(ApiResponse.success("Account statement generated successfully", response));
    }
}