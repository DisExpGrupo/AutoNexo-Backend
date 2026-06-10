package com.atg.autonexo.backend.iam.domain.model.exceptions;

/**
 * Exception thrown when the request is unauthenticated or the
 * authenticated principal cannot be resolved.
 * <p>
 * Mapped to HTTP 401 by {@code IamExceptionHandler}.
 * </p>
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("Authentication required");
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
