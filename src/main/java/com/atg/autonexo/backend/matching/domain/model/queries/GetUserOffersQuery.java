package com.atg.autonexo.backend.matching.domain.model.queries;

import com.atg.autonexo.backend.matching.domain.model.valueobjects.OfferStatus;

/**
 * Query to get offers received by a user (for their service requests).
 */
public record GetUserOffersQuery(
    Long userId,
    OfferStatus status
) {
    public GetUserOffersQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("UserId must be valid");
        }
        // status can be null to get all offers
    }
}

