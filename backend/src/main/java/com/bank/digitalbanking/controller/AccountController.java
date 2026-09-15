package com.bank.digitalbanking.controller;

import com.bank.digitalbanking.dto.request.CreateAccountRequest;
import com.bank.digitalbanking.dto.response.AccountResponse;
import com.bank.digitalbanking.dto.response.ApiResponse;
import com.bank.digitalbanking.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();
        AccountResponse response = accountService.createAccount(userEmail, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bank account created successfully", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getMyAccounts(
            Authentication authentication) {

        String userEmail = authentication.getName();
        List<AccountResponse> response = accountService.getUserAccounts(userEmail);

        return ResponseEntity.ok(ApiResponse.success("Accounts retrieved successfully", response));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountByNumber(
            @PathVariable String accountNumber,
            Authentication authentication) {

        String userEmail = authentication.getName();
        AccountResponse response = accountService.getAccountByNumber(userEmail, accountNumber);

        return ResponseEntity.ok(ApiResponse.success("Account details retrieved successfully", response));
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<String>> deleteAccount(
            @PathVariable String accountNumber,
            Authentication authentication) {

        String userEmail = authentication.getName();
        accountService.deleteAccount(userEmail, accountNumber);

        return ResponseEntity.ok(ApiResponse.success("Bank account closed successfully", accountNumber));
    }
}