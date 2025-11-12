package com.atg.autonexo.backend.trust.domain.exceptions;

/**
 * Exception thrown when attempting to create a duplicate review for a service booking.
 */
public class ReviewAlreadyExistsException extends RuntimeException {
    public ReviewAlreadyExistsException(Long serviceBookingId, Long reviewerId) {
        super(String.format("Review already exists for service booking %d by reviewer %d", 
            serviceBookingId, reviewerId));
    }
}

