package com.CompusLink.CompusLink.exception;

/**
 * Exception thrown when a requested entity is not found in the database.
 * Used for cases where an item, post, offer, or user doesn't exist.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityName, Object id) {
        super(String.format("%s with id '%s' not found", entityName, id));
    }
}
