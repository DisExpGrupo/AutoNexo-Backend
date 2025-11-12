package com.atg.autonexo.backend.trust.domain.model.queries;

import com.atg.autonexo.backend.trust.domain.model.valueobjects.ReviewStatus;

/**
 * Query to get all reviews for a workshop.
 */
public record GetWorkshopReviewsQuery(
    Long workshopId,
    ReviewStatus status,
    Integer page,
    Integer size
) {
    public GetWorkshopReviewsQuery {
        if (workshopId == null || workshopId <= 0) {
            throw new IllegalArgumentException("Workshop ID must be valid");
        }
    }
}

