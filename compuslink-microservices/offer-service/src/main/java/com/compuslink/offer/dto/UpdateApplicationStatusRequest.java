package com.compuslink.offer.dto;

import com.compuslink.offer.model.AppStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateApplicationStatusRequest {

    @NotNull
    private AppStatus status;
}
