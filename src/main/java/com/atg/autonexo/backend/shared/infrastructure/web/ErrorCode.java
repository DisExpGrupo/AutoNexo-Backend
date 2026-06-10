package com.atg.autonexo.backend.shared.infrastructure.web;

/**
 * Machine-readable error codes for client-side handling.
 * <p>
 * The frontend can use these codes to drive internationalization, analytics,
 * or conditional logic without parsing the human-readable {@code message}.
 * </p>
 */
public enum ErrorCode {
    /** Resource or user not found. */
    USER_NOT_FOUND,
    /** Provided credentials are invalid. */
    INVALID_CREDENTIALS,
    /** Attempting to register with an email that is already in use. */
    EMAIL_ALREADY_EXISTS,
    /** Account exists but is deactivated. */
    ACCOUNT_DEACTIVATED,
    /** Authentication is required or has expired. */
    UNAUTHORIZED,
    /** Authenticated user lacks permission. */
    ACCESS_DENIED,
    /** Request payload failed validation. */
    VALIDATION_ERROR,
    /** Password reset token is invalid or expired. */
    INVALID_TOKEN,
    /** Generic catch-all for unexpected server errors. */
    INTERNAL_ERROR
}
