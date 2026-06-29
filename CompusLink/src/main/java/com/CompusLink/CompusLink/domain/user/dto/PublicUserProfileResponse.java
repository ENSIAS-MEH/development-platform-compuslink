package com.CompusLink.CompusLink.domain.user.dto;

import com.CompusLink.CompusLink.domain.user.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO representing the public profile of a user.
 * Returned when viewing another user's profile.
 * Contains only non-sensitive, publicly visible information.
 * Does NOT include: email, phone number, CV, account status, or timestamps.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicUserProfileResponse {

    /**
     * User's unique identifier
     */
    private UUID id;

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
     * User biography or description
     */
    private String bio;

    /**
     * URL to the user's profile picture
     */
    private String profilePicUrl;

    /**
     * User's role (STUDENT, RECRUITER, ADMIN)
     */
    private UserRole role;
}
