package com.atg.autonexo.backend.matching.domain.model.queries;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;

/**
 * Query to get service requests that a workshop has received (has sent offers to).
 */
public record GetWorkshopReceivedRequestsQuery(
    WorkshopId workshopId
) {
    public GetWorkshopReceivedRequestsQuery {
        if (workshopId == null) {
            throw new IllegalArgumentException("WorkshopId cannot be null");
        }
    }
}

