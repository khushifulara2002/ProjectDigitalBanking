package com.bank.digitalbanking.service;

import com.bank.digitalbanking.dto.response.AccountResponse;
import com.bank.digitalbanking.dto.response.AuditLogResponse;
import com.bank.digitalbanking.dto.response.PagedResponse;
import com.bank.digitalbanking.dto.response.UserProfileResponse;

import java.util.List;

public interface AdminService {

    List<UserProfileResponse> getAllUsers();

    List<AccountResponse> getAllAccounts();

    AccountResponse blockAccount(String accountNumber, String reason, String adminEmail, String ipAddress);

    AccountResponse unblockAccount(String accountNumber, String adminEmail, String ipAddress);

    PagedResponse<AuditLogResponse> getAuditLogs(int page, int size);
}