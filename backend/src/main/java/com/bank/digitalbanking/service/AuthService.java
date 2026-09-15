package com.bank.digitalbanking.service;

import com.bank.digitalbanking.dto.request.LoginRequest;
import com.bank.digitalbanking.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);
}