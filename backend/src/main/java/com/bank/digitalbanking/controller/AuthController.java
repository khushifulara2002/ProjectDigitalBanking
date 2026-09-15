package com.bank.digitalbanking.controller;

import com.bank.digitalbanking.dto.request.LoginRequest;
import com.bank.digitalbanking.dto.request.RegisterRequest;
import com.bank.digitalbanking.dto.response.ApiResponse;
import com.bank.digitalbanking.dto.response.AuthResponse;
import com.bank.digitalbanking.dto.response.UserProfileResponse;
import com.bank.digitalbanking.service.AuthService;
import com.bank.digitalbanking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserProfileResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        UserProfileResponse response = userService.registerUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Authentication successful", response));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @Valid @RequestBody com.bank.digitalbanking.dto.request.UpdateProfileRequest request,
            org.springframework.security.core.Authentication authentication) {

        String userEmail = authentication.getName();
        UserProfileResponse response = userService.updateUserProfile(userEmail, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }
}