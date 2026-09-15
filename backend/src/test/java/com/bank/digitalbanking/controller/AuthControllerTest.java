package com.bank.digitalbanking.controller;

import com.bank.digitalbanking.dto.request.LoginRequest;
import com.bank.digitalbanking.dto.response.AuthResponse;
import com.bank.digitalbanking.service.AuthService;
import com.bank.digitalbanking.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/v1/auth/login - Should return 200 OK and JWT token")
    void login_Success() throws Exception {
        // Arrange
        LoginRequest loginRequest = LoginRequest.builder()
                .email("john.doe@gmail.com")
                .password("Password123")
                .build();

        AuthResponse authResponse = AuthResponse.builder()
                .token("mock_jwt_token_string")
                .tokenType("Bearer")
                .email("john.doe@gmail.com")
                .roles(List.of("ROLE_CUSTOMER"))
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.token").value("mock_jwt_token_string"))
                .andExpect(jsonPath("$.data.email").value("john.doe@gmail.com"));
    }
}