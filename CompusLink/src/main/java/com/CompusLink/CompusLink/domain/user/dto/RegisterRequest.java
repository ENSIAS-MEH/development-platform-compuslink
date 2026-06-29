package com.CompusLink.CompusLink.domain.user.dto;

import com.CompusLink.CompusLink.domain.user.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String fullName;

    private UserRole role = UserRole.STUDENT;

    private String university;

    private String city;

    private String phoneNumber;
}
