package com.CompusLink.CompusLink.domain.user.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating user profile information.
 * All fields are optional - only the fields provided will be updated (PATCH semantics).
 * Fields not included in the request will remain unchanged.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    /**
     * User's full name
     */
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String fullName;

    /**
     * University or educational institution
     */
    @Size(max = 255, message = "University must not exceed 255 characters")
    private String university;

    /**
     * City of residence
     */
    @Size(max = 255, message = "City must not exceed 255 characters")
    private String city;

    /**
     * User biography or description
     */
    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;

    /**
     * Phone number
     */
    @Size(max = 50, message = "Phone number must not exceed 50 characters")
    private String phoneNumber;
}
