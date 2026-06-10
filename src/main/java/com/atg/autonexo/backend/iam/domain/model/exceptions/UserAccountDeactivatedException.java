package com.atg.autonexo.backend.iam.domain.model.exceptions;

/**
 * Exception thrown when an operation targets a deactivated user account.
 * <p>
 * Mapped to HTTP 403 by {@code IamExceptionHandler}.
 * </p>
 */
public class UserAccountDeactivatedException extends RuntimeException {

    public UserAccountDeactivatedException() {
        super("This account has been deactivated");
    }

    public UserAccountDeactivatedException(String message) {
        super(message);
    }

    public UserAccountDeactivatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
