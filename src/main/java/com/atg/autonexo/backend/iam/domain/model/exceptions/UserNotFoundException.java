package com.atg.autonexo.backend.iam.domain.model.exceptions;

/**
 * Exception thrown when a user is not found.
 * <p>
 * Mapped to HTTP 404 by {@code IamExceptionHandler}. The message does
 * not include the identifier to avoid leaking which accounts exist.
 * </p>
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("User not found");
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
