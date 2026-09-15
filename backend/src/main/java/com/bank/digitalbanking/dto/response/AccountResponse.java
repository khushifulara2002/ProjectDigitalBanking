package com.bank.digitalbanking.dto.response;

import com.bank.digitalbanking.entity.AccountStatus;
import com.bank.digitalbanking.entity.AccountType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {

    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private AccountStatus status;
    private String blockReason;
    private String ownerName;
    private String userEmail;
    private LocalDateTime createdAt;
}