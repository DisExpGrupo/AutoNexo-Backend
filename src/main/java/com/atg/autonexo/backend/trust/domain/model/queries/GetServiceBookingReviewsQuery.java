package com.atg.autonexo.backend.trust.domain.model.queries;

/**
 * Query to get both reviews (user->workshop and workshop->user) for a service booking.
 */
public record GetServiceBookingReviewsQuery(
    Long serviceBookingId
) {
    public GetServiceBookingReviewsQuery {
        if (serviceBookingId == null || serviceBookingId <= 0) {
            throw new IllegalArgumentException("Service booking ID must be valid");
        }
    }
}

