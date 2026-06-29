package com.compuslink.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @Size(max = 255)
    private String fullName;
    @Size(max = 255)
    private String university;
    @Size(max = 255)
    private String city;
    @Size(max = 50)
    private String phoneNumber;
    @Size(max = 2000)
    private String bio;
}
