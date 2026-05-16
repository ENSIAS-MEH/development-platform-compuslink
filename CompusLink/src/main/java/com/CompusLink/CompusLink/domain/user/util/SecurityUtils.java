package com.CompusLink.CompusLink.domain.user.util;

import com.CompusLink.CompusLink.domain.user.model.UserPrincipal;
import com.CompusLink.CompusLink.domain.user.model.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/**
 * Utility class for accessing information about the currently authenticated user.
 * Provides methods to retrieve user ID, email, and role from the Spring Security context.
 */
public class SecurityUtils {

    private SecurityUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Retrieves the UserPrincipal from the current security context.
     *
     * @return the UserPrincipal of the authenticated user, or null if not authenticated
     */
    private static UserPrincipal getUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal) {
            return (UserPrincipal) principal;
        }

        return null;
    }

    /**
     * Gets the ID of the currently authenticated user.
     *
     * @return the user ID
     * @throws IllegalStateException if no user is currently authenticated
     */
    public static UUID getCurrentUserId() {
        UserPrincipal principal = getUserPrincipal();

        if (principal == null) {
            throw new IllegalStateException("No authenticated user found in security context");
        }

        return principal.getUser().getId();
    }

    /**
     * Gets the ID of the currently authenticated user, or null if not authenticated.
     * This is a "silent" version that doesn't throw an exception.
     *
     * @return the user ID, or null if no user is authenticated
     */
    public static UUID getCurrentUserIdOrNull() {
        UserPrincipal principal = getUserPrincipal();
        return principal != null ? principal.getUser().getId() : null;
    }

    /**
     * Gets the email of the currently authenticated user.
     *
     * @return the user email
     * @throws IllegalStateException if no user is currently authenticated
     */
    public static String getCurrentUserEmail() {
        UserPrincipal principal = getUserPrincipal();

        if (principal == null) {
            throw new IllegalStateException("No authenticated user found in security context");
        }

        return principal.getUser().getEmail();
    }

    /**
     * Gets the email of the currently authenticated user, or null if not authenticated.
     * This is a "silent" version that doesn't throw an exception.
     *
     * @return the user email, or null if no user is authenticated
     */
    public static String getCurrentUserEmailOrNull() {
        UserPrincipal principal = getUserPrincipal();
        return principal != null ? principal.getUser().getEmail() : null;
    }

    /**
     * Gets the role of the currently authenticated user.
     *
     * @return the user role
     * @throws IllegalStateException if no user is currently authenticated
     */
    public static UserRole getCurrentUserRole() {
        UserPrincipal principal = getUserPrincipal();

        if (principal == null) {
            throw new IllegalStateException("No authenticated user found in security context");
        }

        return principal.getUser().getRole();
    }

    /**
     * Gets the role of the currently authenticated user, or null if not authenticated.
     * This is a "silent" version that doesn't throw an exception.
     *
     * @return the user role, or null if no user is authenticated
     */
    public static UserRole getCurrentUserRoleOrNull() {
        UserPrincipal principal = getUserPrincipal();
        return principal != null ? principal.getUser().getRole() : null;
    }

    /**
     * Checks if a user is currently authenticated.
     *
     * @return true if a user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        return getUserPrincipal() != null;
    }
}
