package com.atg.autonexo.backend.trust.domain.model.queries;

/**
 * Query to get a review by service booking and reviewer.
 */
public record GetReviewByServiceBookingAndReviewerQuery(
    Long serviceBookingId,
    Long reviewerId
) {
    public GetReviewByServiceBookingAndReviewerQuery {
        if (serviceBookingId == null || serviceBookingId <= 0) {
            throw new IllegalArgumentException("Service booking ID must be valid");
        }
        if (reviewerId == null || reviewerId <= 0) {
            throw new IllegalArgumentException("Reviewer ID must be valid");
        }
    }
}

