// 10. UserProfileResponse.java
package com.bank.digitalbanking.dto.response;

import com.bank.digitalbanking.entity.UserStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserStatus status;
    private List<String> roles;
    private LocalDateTime createdAt;
}