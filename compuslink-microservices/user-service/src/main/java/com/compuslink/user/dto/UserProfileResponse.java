package com.compuslink.user.dto;

import com.compuslink.user.model.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class UserProfileResponse {
    private UUID id;
    private String email;
    private UserRole role;
    private String fullName;
    private String university;
    private String city;
    private String phoneNumber;
    private String bio;
    private String profilePicUrl;
    private String cvUrl;
    private boolean isActive;
    private boolean isVerified;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
