package com.compuslink.user.dto;

import com.compuslink.user.model.UserRole;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PublicUserProfileResponse {
    private UUID id;
    private String fullName;
    private String university;
    private String city;
    private String bio;
    private String profilePicUrl;
    private UserRole role;
}
