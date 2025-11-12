package com.atg.autonexo.backend.matching.domain.model.queries;

import com.atg.autonexo.backend.matching.domain.model.valueobjects.OfferStatus;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;

/**
 * Query to get offers sent by a workshop.
 */
public record GetWorkshopOffersQuery(
    WorkshopId workshopId,
    OfferStatus status
) {
    public GetWorkshopOffersQuery {
        if (workshopId == null) {
            throw new IllegalArgumentException("WorkshopId cannot be null");
        }
        // status can be null to get all offers
    }
}

