package com.atg.autonexo.backend.trust.domain.exceptions;

/**
 * Exception thrown when a review is not found.
 */
public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(Long reviewId) {
        super(String.format("Review with id %d not found", reviewId));
    }
}

