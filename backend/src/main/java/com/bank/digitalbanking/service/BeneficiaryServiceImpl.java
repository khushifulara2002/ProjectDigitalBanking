package com.bank.digitalbanking.service;

import com.bank.digitalbanking.dto.request.AddBeneficiaryRequest;
import com.bank.digitalbanking.dto.response.BeneficiaryResponse;
import com.bank.digitalbanking.entity.Beneficiary;
import com.bank.digitalbanking.entity.User;
import com.bank.digitalbanking.exception.BadRequestException;
import com.bank.digitalbanking.exception.ResourceNotFoundException;
import com.bank.digitalbanking.exception.UnauthorizedException;
import com.bank.digitalbanking.repository.BeneficiaryRepository;
import com.bank.digitalbanking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BeneficiaryResponse addBeneficiary(String userEmail, AddBeneficiaryRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        // Prevent Duplicate Beneficiary Account for Same User
        if (beneficiaryRepository.existsByUserIdAndAccountNumber(user.getId(), request.getAccountNumber())) {
            throw new BadRequestException("Beneficiary account " + request.getAccountNumber() + " is already in your payee list");
        }

        Beneficiary beneficiary = Beneficiary.builder()
                .user(user)
                .name(request.getName())
                .accountNumber(request.getAccountNumber())
                .bankName(request.getBankName())
                .build();

        Beneficiary savedBeneficiary = beneficiaryRepository.save(beneficiary);
        return mapToBeneficiaryResponse(savedBeneficiary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BeneficiaryResponse> getUserBeneficiaries(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        return beneficiaryRepository.findByUserId(user.getId()).stream()
                .map(this::mapToBeneficiaryResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteBeneficiary(String userEmail, Long beneficiaryId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with ID: " + beneficiaryId));

        if (!beneficiary.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Access denied: Beneficiary does not belong to you");
        }

        beneficiaryRepository.delete(beneficiary);
    }

    private BeneficiaryResponse mapToBeneficiaryResponse(Beneficiary b) {
        return BeneficiaryResponse.builder()
                .id(b.getId())
                .name(b.getName())
                .accountNumber(b.getAccountNumber())
                .bankName(b.getBankName())
                .createdAt(b.getCreatedAt())
                .build();
    }
}