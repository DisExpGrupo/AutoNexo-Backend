package com.atg.autonexo.backend.trust.domain.model.queries;

/**
 * Query to check if a user can create a review for a service booking.
 */
public record GetReviewWindowStatusQuery(
    Long serviceBookingId,
    Long userId
) {
    public GetReviewWindowStatusQuery {
        if (serviceBookingId == null || serviceBookingId <= 0) {
            throw new IllegalArgumentException("Service booking ID must be valid");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be valid");
        }
    }
}

