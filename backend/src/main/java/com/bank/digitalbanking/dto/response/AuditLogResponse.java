package com.bank.digitalbanking.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {

    private Long id;
    private String performedByEmail;
    private String action;
    private String details;
    private String ipAddress;
    private LocalDateTime createdAt;
}