package com.CompusLink.CompusLink.domain.offer.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class ApplyRequest {

    @Size(max = 1000)
    private String message;

    private UUID cvId;
}
