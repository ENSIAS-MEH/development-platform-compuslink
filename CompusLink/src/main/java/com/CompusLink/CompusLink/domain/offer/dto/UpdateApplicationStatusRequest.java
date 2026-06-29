package com.CompusLink.CompusLink.domain.offer.dto;

import com.CompusLink.CompusLink.domain.offer.model.AppStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateApplicationStatusRequest {

    @NotNull
    private AppStatus status;
}
