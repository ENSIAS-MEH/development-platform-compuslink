package com.CompusLink.CompusLink.exception;

/**
 * Exception thrown when attempting to create a duplicate resource.
 * Used for cases like expressing interest twice, applying twice to the same offer,
 * or saving the same item twice.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resource, String identifier) {
        super(String.format("You have already %s this %s",
            resource.toLowerCase().contains("interest") ? "expressed interest in" :
            resource.toLowerCase().contains("application") ? "applied to" :
            resource.toLowerCase().contains("saved") ? "saved" : "created",
            identifier));
    }
}
