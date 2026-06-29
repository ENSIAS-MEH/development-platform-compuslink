package com.compuslink.user.dto;

import com.compuslink.user.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank @Email
    private String email;
    @NotBlank
    private String password;
    private String fullName;
    private UserRole role;
    private String university;
    private String city;
    private String phoneNumber;
}
