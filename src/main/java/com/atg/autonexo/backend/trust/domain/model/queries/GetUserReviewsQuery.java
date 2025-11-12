package com.atg.autonexo.backend.trust.domain.model.queries;

import com.atg.autonexo.backend.trust.domain.model.valueobjects.ReviewStatus;

/**
 * Query to get all reviews for a user.
 */
public record GetUserReviewsQuery(
    Long userId,
    ReviewStatus status,
    Integer page,
    Integer size
) {
    public GetUserReviewsQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be valid");
        }
    }
}

