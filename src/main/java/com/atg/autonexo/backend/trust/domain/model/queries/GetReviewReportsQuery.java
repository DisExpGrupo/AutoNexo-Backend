package com.atg.autonexo.backend.trust.domain.model.queries;

/**
 * Query to get all reports for a specific review.
 */
public record GetReviewReportsQuery(
    Long reviewId
) {
    public GetReviewReportsQuery {
        if (reviewId == null || reviewId <= 0) {
            throw new IllegalArgumentException("Review ID must be valid");
        }
    }
}

