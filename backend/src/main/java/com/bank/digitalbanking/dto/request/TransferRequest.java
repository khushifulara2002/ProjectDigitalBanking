// 6. TransferRequest.java
package com.bank.digitalbanking.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {

    @NotBlank(message = "Source account number is required")
    private String sourceAccountNumber;

    @NotBlank(message = "Target account number is required")
    private String targetAccountNumber;

    @NotNull(message = "Transfer amount is required")
    @Positive(message = "Transfer amount must be greater than 0")
    private BigDecimal amount;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
}