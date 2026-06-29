package com.CompusLink.CompusLink.exception;

/**
 * Exception thrown when a business rule is violated.
 * Used for cases like:
 * - Applying to your own offer
 * - Reopening a closed listing
 * - Confirming more spots than available
 * - Any other domain-specific business constraint violations
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
