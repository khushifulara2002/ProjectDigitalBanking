package com.bank.digitalbanking.service;

import com.bank.digitalbanking.dto.request.DepositRequest;
import com.bank.digitalbanking.dto.request.TransferRequest;
import com.bank.digitalbanking.dto.request.WithdrawRequest;
import com.bank.digitalbanking.dto.response.PagedResponse;
import com.bank.digitalbanking.dto.response.TransactionResponse;

public interface TransactionService {

    TransactionResponse deposit(String userEmail, DepositRequest request);

    TransactionResponse withdraw(String userEmail, WithdrawRequest request);

    TransactionResponse transfer(String userEmail, TransferRequest request);

    PagedResponse<TransactionResponse> getAccountTransactions(
            String userEmail, String accountNumber, int page, int size);

    // Add this method signature inside TransactionService interface:
    PagedResponse<TransactionResponse> getFilteredTransactions(
            String userEmail,
            String accountNumber,
            com.bank.digitalbanking.entity.TransactionType type,
            java.time.LocalDateTime startDate,
            java.time.LocalDateTime endDate,
            int page,
            int size
    );
}