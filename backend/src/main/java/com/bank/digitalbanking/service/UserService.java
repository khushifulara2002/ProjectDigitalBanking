package com.bank.digitalbanking.service;

import com.bank.digitalbanking.dto.request.RegisterRequest;
import com.bank.digitalbanking.dto.request.UpdateProfileRequest;
import com.bank.digitalbanking.dto.response.UserProfileResponse;
import com.bank.digitalbanking.entity.User;

import java.util.List;

public interface UserService {

    UserProfileResponse registerUser(RegisterRequest request);

    UserProfileResponse getUserProfileByEmail(String email);

    UserProfileResponse getUserProfileById(Long userId);

    UserProfileResponse updateUserProfile(String currentEmail, UpdateProfileRequest request);

    User getUserEntityByEmail(String email);

    User getUserEntityById(Long userId);

    List<UserProfileResponse> getAllUsers();
}