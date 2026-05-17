package com.CompusLink.CompusLink.domain.user.dto;

import com.CompusLink.CompusLink.domain.user.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO representing the complete user profile.
 * Returned to the authenticated user when they request their own profile.
 * Contains all user information except sensitive data (password hash, refresh token hash).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    /**
     * User's unique identifier
     */
    private UUID id;

    /**
     * User's email address
     */
    private String email;

    /**
     * User's role (STUDENT, RECRUITER, ADMIN)
     */
    private UserRole role;

    /**
     * User's full name
     */
    private String fullName;

    /**
     * University or educational institution
     */
    private String university;

    /**
     * City of residence
     */
    private String city;

    /**
     * Phone number
     */
    private String phoneNumber;

    /**
     * User biography or description
     */
    private String bio;

    /**
     * URL to the user's profile picture
     */
    private String profilePicUrl;

    /**
     * URL to the user's CV/resume
     */
    private String cvUrl;

    /**
     * Whether the user account is active
     */
    private boolean isActive;

    /**
     * Whether the user's email is verified
     */
    private boolean isVerified;

    /**
     * Account creation timestamp
     */
    private OffsetDateTime createdAt;

    /**
     * Last update timestamp
     */
    private OffsetDateTime updatedAt;
}
