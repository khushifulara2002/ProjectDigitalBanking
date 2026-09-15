package com.bank.digitalbanking.service;

import com.bank.digitalbanking.dto.request.LoginRequest;
import com.bank.digitalbanking.dto.response.AuthResponse;
import com.bank.digitalbanking.entity.Role;
import com.bank.digitalbanking.entity.User;
import com.bank.digitalbanking.exception.UnauthorizedException;
import com.bank.digitalbanking.repository.UserRepository;
import com.bank.digitalbanking.security.CustomUserDetails;
import com.bank.digitalbanking.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            // 1. Perform Authentication via Spring Security AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail().toLowerCase().trim(),
                            request.getPassword()
                    )
            );

            // 2. Extract Authenticated UserDetails & User Entity
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            // 3. Generate JWT Token
            String jwtToken = jwtService.generateToken(userDetails);

            // 4. Map Roles to String List
            List<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .toList();

            // 5. Build and return AuthResponse DTO
            return AuthResponse.builder()
                    .token(jwtToken)
                    .tokenType("Bearer")
                    .id(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .roles(roles)
                    .build();

        } catch (BadCredentialsException ex) {
            throw new UnauthorizedException("Invalid email or password");
        }
    }
}