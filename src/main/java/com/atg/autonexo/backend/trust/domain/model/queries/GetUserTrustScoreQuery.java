package com.atg.autonexo.backend.trust.domain.model.queries;

/**
 * Query to get trust score and statistics for a user.
 */
public record GetUserTrustScoreQuery(
    Long userId
) {
    public GetUserTrustScoreQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be valid");
        }
    }
}

