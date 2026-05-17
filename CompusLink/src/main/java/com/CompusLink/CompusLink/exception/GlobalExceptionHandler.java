package com.CompusLink.CompusLink.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global exception handler that intercepts all exceptions in the application
 * and returns uniform JSON error responses with appropriate HTTP status codes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== Custom Business Exceptions ====================

    /**
     * Handles EntityNotFoundException.
     * Returns 404 NOT FOUND when a requested entity doesn't exist.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEntityNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    /**
     * Handles AccessDeniedException.
     * Returns 403 FORBIDDEN when a user tries to access/modify content they don't own.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request, null);
    }

    /**
     * Handles DuplicateResourceException.
     * Returns 409 CONFLICT when attempting to create a duplicate resource
     * (e.g., expressing interest twice, applying twice, saving twice).
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    /**
     * Handles BusinessRuleException.
     * Returns 422 UNPROCESSABLE ENTITY when a business rule is violated
     * (e.g., applying to own offer, reopening closed listing, exceeding available spots).
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessRuleViolation(
            BusinessRuleException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request, null);
    }

    // ==================== Validation & Database Exceptions ====================

    /**
     * Handles MethodArgumentNotValidException.
     * Returns 400 BAD REQUEST with all field validation errors when request validation fails.
     * Collects ALL invalid fields, not just the first one.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage()));

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", request, fieldErrors);
    }

    /**
     * Handles DataIntegrityViolationException.
     * Returns 409 CONFLICT when database constraints are violated
     * (e.g., unique constraint, foreign key constraint).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        String message = "Database constraint violation";

        // Try to provide a more specific message for common constraint violations
        String rootCause = ex.getMostSpecificCause().getMessage();
        if (rootCause != null) {
            if (rootCause.contains("duplicate key") || rootCause.contains("unique constraint")) {
                message = "A record with this value already exists";
            } else if (rootCause.contains("foreign key")) {
                message = "Cannot perform this operation due to related records";
            } else if (rootCause.contains("not-null")) {
                message = "Required field is missing";
            }
        }

        return buildResponse(HttpStatus.CONFLICT, message, request, null);
    }

    // ==================== General Exceptions ====================

    /**
     * Handles IllegalArgumentException.
     * Returns 400 BAD REQUEST for invalid arguments.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    /**
     * Handles BadCredentialsException.
     * Returns 401 UNAUTHORIZED for authentication failures.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password", request, null);
    }

    /**
     * Handles IllegalStateException.
     * Returns 409 CONFLICT for illegal state operations.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    /**
     * Catches all other exceptions not explicitly handled above.
     * Returns 500 INTERNAL SERVER ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        // Log the exception for debugging (in production, use proper logging framework)
        ex.printStackTrace();

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request,
                null
        );
    }

    // ==================== Helper Methods ====================

    /**
     * Builds a uniform ApiErrorResponse with the given parameters.
     */
    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> fieldErrors
    ) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
