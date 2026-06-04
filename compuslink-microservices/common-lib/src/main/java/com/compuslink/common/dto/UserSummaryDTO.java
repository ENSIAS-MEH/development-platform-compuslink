package com.compuslink.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSummaryDTO {
    private UUID id;
    private String fullName;
    private String email;
    private String profilePicUrl;
}
