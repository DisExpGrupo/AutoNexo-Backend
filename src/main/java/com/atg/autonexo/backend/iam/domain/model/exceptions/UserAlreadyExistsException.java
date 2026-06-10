package com.atg.autonexo.backend.iam.domain.model.exceptions;

/**
 * Exception thrown when attempting to create a user that already exists.
 * <p>
 * Mapped to HTTP 409 by {@code IamExceptionHandler}. The message does
 * not echo the email back to the client.
 * </p>
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException() {
        super("An account with this email already exists");
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
