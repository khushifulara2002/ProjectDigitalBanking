// 3. CreateAccountRequest.java
package com.bank.digitalbanking.dto.request;

import com.bank.digitalbanking.entity.AccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountRequest {

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotNull(message = "Initial deposit is required")
    @PositiveOrZero(message = "Initial deposit must be greater than or equal to 0")
    private BigDecimal initialDeposit;
}