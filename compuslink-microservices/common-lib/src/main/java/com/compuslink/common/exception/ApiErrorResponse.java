package com.compuslink.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private int status;
    private String message;
    private OffsetDateTime timestamp;
}
