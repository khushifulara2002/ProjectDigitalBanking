// 9. AuthResponse.java
package com.bank.digitalbanking.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    @Builder.Default
    private String tokenType = "Bearer";
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
}