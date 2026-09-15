package com.bank.digitalbanking.service;

import com.bank.digitalbanking.dto.request.RegisterRequest;
import com.bank.digitalbanking.dto.request.UpdateProfileRequest;
import com.bank.digitalbanking.dto.response.UserProfileResponse;
import com.bank.digitalbanking.entity.Role;
import com.bank.digitalbanking.entity.User;
import com.bank.digitalbanking.entity.UserStatus;
import com.bank.digitalbanking.exception.BadRequestException;
import com.bank.digitalbanking.exception.ResourceNotFoundException;
import com.bank.digitalbanking.repository.RoleRepository;
import com.bank.digitalbanking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserProfileResponse registerUser(RegisterRequest request) {
        // 1. Validate if Email is already registered
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email address '" + request.getEmail() + "' is already registered");
        }

        // 2. Validate if Phone Number is already registered
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("Phone number '" + request.getPhoneNumber() + "' is already registered");
        }

        // 3. Fetch default ROLE_CUSTOMER from DB
        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("Default role 'ROLE_CUSTOMER' not found in database"));

        // 4. Create and populate User Entity
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt Hash
                .phoneNumber(request.getPhoneNumber())
                .status(UserStatus.ACTIVE)
                .roles(Collections.singleton(customerRole))
                .build();

        // 5. Save to MySQL
        User savedUser = userRepository.save(user);

        // 6. Map saved entity to UserProfileResponse DTO
        return mapToUserProfileResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileByEmail(String email) {
        User user = getUserEntityByEmail(email);
        return mapToUserProfileResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileById(Long userId) {
        User user = getUserEntityById(userId);
        return mapToUserProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(String currentEmail, UpdateProfileRequest request) {
        User user = getUserEntityByEmail(currentEmail);

        // 1. Security Check: Current Password Verification
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password verification failed. Security authentication rejected.");
        }

        // 2. Update Basic Profile Info
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        // Phone Number Uniqueness Check
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber().trim())) {
                throw new BadRequestException("Phone number " + request.getPhoneNumber() + " is already registered.");
            }
            user.setPhoneNumber(request.getPhoneNumber().trim());
        }

        // 3. Email Update Check
        if (!user.getEmail().equalsIgnoreCase(request.getEmail().trim())) {
            if (userRepository.existsByEmail(request.getEmail().trim())) {
                throw new BadRequestException("Email " + request.getEmail() + " is already taken by another account.");
            }
            user.setEmail(request.getEmail().trim().toLowerCase());
        }

        // 4. Optional Password Update
        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword().trim()));
        }

        User savedUser = userRepository.save(user);
        return mapToUserProfileResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserProfileResponse)
                .toList();
    }

    // Helper Method: Entity -> DTO Mapper
    private UserProfileResponse mapToUserProfileResponse(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        return UserProfileResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .roles(roleNames)
                .createdAt(user.getCreatedAt())
                .build();
    }
}