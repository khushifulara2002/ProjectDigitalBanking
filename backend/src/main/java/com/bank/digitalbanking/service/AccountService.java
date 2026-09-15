package com.bank.digitalbanking.service;

import com.bank.digitalbanking.dto.request.CreateAccountRequest;
import com.bank.digitalbanking.dto.response.AccountResponse;
import com.bank.digitalbanking.entity.Account;
import com.bank.digitalbanking.entity.AccountStatus;

import java.util.List;

public interface AccountService {

    AccountResponse createAccount(String userEmail, CreateAccountRequest request);

    List<AccountResponse> getUserAccounts(String userEmail);

    AccountResponse getAccountByNumber(String userEmail, String accountNumber);

    void deleteAccount(String userEmail, String accountNumber);

    Account getAccountEntityByNumber(String accountNumber);

    AccountResponse updateAccountStatus(String accountNumber, AccountStatus status);

    List<AccountResponse> getAllAccounts(); // Admin
}