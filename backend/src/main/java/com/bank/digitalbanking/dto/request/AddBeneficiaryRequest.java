// 7. AddBeneficiaryRequest.java
package com.bank.digitalbanking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddBeneficiaryRequest {

    @NotBlank(message = "Beneficiary name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Beneficiary account number is required")
    private String accountNumber;

    @NotBlank(message = "Bank name is required")
    private String bankName;
}