package com.atg.autonexo.backend.iam.domain.model.exceptions;

/**
 * Exception thrown when a verification or password-reset token is
 * invalid, expired, or already consumed.
 * <p>
 * Mapped to HTTP 400 by {@code IamExceptionHandler} with
 * {@code ErrorCode.INVALID_TOKEN}.
 * </p>
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("The provided token is invalid or has expired");
    }

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
