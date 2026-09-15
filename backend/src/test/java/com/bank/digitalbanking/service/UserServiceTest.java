package com.bank.digitalbanking.service;

import com.bank.digitalbanking.dto.request.RegisterRequest;
import com.bank.digitalbanking.dto.response.UserProfileResponse;
import com.bank.digitalbanking.entity.Role;
import com.bank.digitalbanking.entity.User;
import com.bank.digitalbanking.entity.UserStatus;
import com.bank.digitalbanking.exception.BadRequestException;
import com.bank.digitalbanking.repository.RoleRepository;
import com.bank.digitalbanking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest registerRequest;
    private Role customerRole;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@gmail.com")
                .password("Password123")
                .phoneNumber("+1234567890")
                .build();

        customerRole = Role.builder()
                .id(1L)
                .name("ROLE_CUSTOMER")
                .build();
    }

    @Test
    @DisplayName("Should successfully register a new customer")
    void registerUser_Success() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(roleRepository.findByName("ROLE_CUSTOMER")).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password_hash");

        User savedUser = User.builder()
                .id(10L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@gmail.com")
                .phoneNumber("+1234567890")
                .status(UserStatus.ACTIVE)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserProfileResponse response = userService.registerUser(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("john.doe@gmail.com", response.getEmail());
        assertEquals("John", response.getFirstName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when email is already registered")
    void registerUser_DuplicateEmail_ThrowsException() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.registerUser(registerRequest);
        });

        assertTrue(exception.getMessage().contains("already registered"));
        verify(userRepository, never()).save(any(User.class));
    }
}