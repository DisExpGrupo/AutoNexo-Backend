package com.atg.autonexo.backend.matching.domain.model.queries;

import com.atg.autonexo.backend.matching.domain.model.valueobjects.ServiceBookingStatus;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;

/**
 * Query to get service bookings for a workshop.
 */
public record GetWorkshopServiceBookingsQuery(
    WorkshopId workshopId,
    ServiceBookingStatus status
) {
    public GetWorkshopServiceBookingsQuery {
        if (workshopId == null) {
            throw new IllegalArgumentException("WorkshopId cannot be null");
        }
        // status can be null to get all bookings
    }
}

