package com.CompusLink.CompusLink.domain.offer.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApplyRequest {

    @Size(max = 1000)
    private String message;
}
