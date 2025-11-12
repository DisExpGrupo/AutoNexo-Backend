package com.atg.autonexo.backend.trust.domain.exceptions;

import java.time.LocalDateTime;

/**
 * Exception thrown when attempting to create a review after the 14-day window has expired.
 */
public class ReviewWindowExpiredException extends RuntimeException {
    public ReviewWindowExpiredException(LocalDateTime windowExpiresAt) {
        super(String.format("Review window expired at %s", windowExpiresAt));
    }
}

