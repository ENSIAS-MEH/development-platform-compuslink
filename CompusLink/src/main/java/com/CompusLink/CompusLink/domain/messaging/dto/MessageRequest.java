package com.CompusLink.CompusLink.domain.messaging.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageRequest {

    @NotBlank
    private String content;
}
