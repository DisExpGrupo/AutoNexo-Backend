package com.atg.autonexo.backend.iam.domain.model.exceptions;

/**
 * Exception thrown when authentication credentials are invalid.
 * <p>
 * Mapped to HTTP 401 by {@code IamExceptionHandler}. The message is
 * intentionally generic to avoid leaking whether the email exists.
 * </p>
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
