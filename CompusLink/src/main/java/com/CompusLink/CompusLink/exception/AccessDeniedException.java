package com.CompusLink.CompusLink.exception;

/**
 * Exception thrown when a user attempts to access or modify content they don't own.
 * Used for authorization checks where the user is authenticated but not authorized
 * to perform the specific action.
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException() {
        super("You do not have permission to perform this action");
    }
}
