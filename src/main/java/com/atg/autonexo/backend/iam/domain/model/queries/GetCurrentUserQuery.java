package com.atg.autonexo.backend.iam.domain.model.queries;

/**
 * Query to get the current authenticated user.
 */
public record GetCurrentUserQuery(Long userId) {
    public GetCurrentUserQuery {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
}


