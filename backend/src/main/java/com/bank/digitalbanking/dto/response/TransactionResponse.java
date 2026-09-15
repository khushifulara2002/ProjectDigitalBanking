package com.bank.digitalbanking.dto.response;

import com.bank.digitalbanking.entity.TransactionStatus;
import com.bank.digitalbanking.entity.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;
    private String transactionReference;
    private String sourceAccountNumber;
    private String targetAccountNumber;
    private String sourceAccountOwnerName;
    private String targetAccountOwnerName;
    private TransactionType type;
    private BigDecimal amount;
    private TransactionStatus status;
    private String description;
    private LocalDateTime createdAt;
}