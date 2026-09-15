package com.bank.digitalbanking.service;

import com.bank.digitalbanking.dto.request.AddBeneficiaryRequest;
import com.bank.digitalbanking.dto.response.BeneficiaryResponse;

import java.util.List;

public interface BeneficiaryService {

    BeneficiaryResponse addBeneficiary(String userEmail, AddBeneficiaryRequest request);

    List<BeneficiaryResponse> getUserBeneficiaries(String userEmail);

    void deleteBeneficiary(String userEmail, Long beneficiaryId);
}