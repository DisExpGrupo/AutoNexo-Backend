package com.atg.autonexo.backend.trust.domain.model.queries;

/**
 * Query to get trust score and statistics for a workshop.
 */
public record GetWorkshopTrustScoreQuery(
    Long workshopId
) {
    public GetWorkshopTrustScoreQuery {
        if (workshopId == null || workshopId <= 0) {
            throw new IllegalArgumentException("Workshop ID must be valid");
        }
    }
}

