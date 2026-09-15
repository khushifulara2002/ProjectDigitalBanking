// 13. BeneficiaryResponse.java
package com.bank.digitalbanking.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiaryResponse {

    private Long id;
    private String name;
    private String accountNumber;
    private String bankName;
    private LocalDateTime createdAt;
}