package com.bank.digitalbanking.controller;

import com.bank.digitalbanking.dto.response.AccountResponse;
import com.bank.digitalbanking.dto.response.ApiResponse;
import com.bank.digitalbanking.dto.response.AuditLogResponse;
import com.bank.digitalbanking.dto.response.PagedResponse;
import com.bank.digitalbanking.dto.response.UserProfileResponse;
import com.bank.digitalbanking.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserProfileResponse>>> getAllUsers() {
        List<UserProfileResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("All users retrieved", users));
    }

    @GetMapping("/accounts")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAllAccounts() {
        List<AccountResponse> accounts = adminService.getAllAccounts();
        return ResponseEntity.ok(ApiResponse.success("All accounts retrieved", accounts));
    }

    @PutMapping("/accounts/{accountNumber}/block")
    public ResponseEntity<ApiResponse<AccountResponse>> blockAccount(
            @PathVariable String accountNumber,
            @RequestParam(required = false) String reason,
            Authentication authentication,
            HttpServletRequest request) {

        String adminEmail = authentication.getName();
        String ipAddress = request.getRemoteAddr();
        AccountResponse response = adminService.blockAccount(accountNumber, reason, adminEmail, ipAddress);

        return ResponseEntity.ok(ApiResponse.success("Account frozen successfully", response));
    }

    @PutMapping("/accounts/{accountNumber}/unblock")
    public ResponseEntity<ApiResponse<AccountResponse>> unblockAccount(
            @PathVariable String accountNumber,
            Authentication authentication,
            HttpServletRequest request) {

        String adminEmail = authentication.getName();
        String ipAddress = request.getRemoteAddr();
        AccountResponse response = adminService.unblockAccount(accountNumber, adminEmail, ipAddress);

        return ResponseEntity.ok(ApiResponse.success("Account activated successfully", response));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<PagedResponse<AuditLogResponse>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PagedResponse<AuditLogResponse> response = adminService.getAuditLogs(page, size);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved", response));
    }
}