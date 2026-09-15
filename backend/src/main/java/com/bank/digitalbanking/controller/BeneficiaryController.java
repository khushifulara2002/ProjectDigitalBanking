package com.bank.digitalbanking.controller;

import com.bank.digitalbanking.dto.request.AddBeneficiaryRequest;
import com.bank.digitalbanking.dto.response.ApiResponse;
import com.bank.digitalbanking.dto.response.BeneficiaryResponse;
import com.bank.digitalbanking.service.BeneficiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficiaries")
@RequiredArgsConstructor
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @PostMapping
    public ResponseEntity<ApiResponse<BeneficiaryResponse>> addBeneficiary(
            @Valid @RequestBody AddBeneficiaryRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();
        BeneficiaryResponse response = beneficiaryService.addBeneficiary(userEmail, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Beneficiary added successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BeneficiaryResponse>>> getMyBeneficiaries(
            Authentication authentication) {

        String userEmail = authentication.getName();
        List<BeneficiaryResponse> response = beneficiaryService.getUserBeneficiaries(userEmail);
        return ResponseEntity.ok(ApiResponse.success("Beneficiaries retrieved successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBeneficiary(
            @PathVariable Long id,
            Authentication authentication) {

        String userEmail = authentication.getName();
        beneficiaryService.deleteBeneficiary(userEmail, id);
        return ResponseEntity.ok(ApiResponse.success("Beneficiary deleted successfully", null));
    }
}