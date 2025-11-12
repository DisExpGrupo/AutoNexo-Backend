package com.atg.autonexo.backend.matching.domain.model.queries;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;

/**
 * Query to get available service requests for a workshop (matching).
 */
public record GetWorkshopAvailableRequestsQuery(
    WorkshopId workshopId
) {
    public GetWorkshopAvailableRequestsQuery {
        if (workshopId == null) {
            throw new IllegalArgumentException("WorkshopId cannot be null");
        }
    }
}

