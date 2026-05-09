package com.CompusLink.CompusLink.domain.user.dto;

import com.CompusLink.CompusLink.domain.user.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserResponse {

    private UUID userId;

    private String email;

    private String fullName;

    private UserRole role;
}
