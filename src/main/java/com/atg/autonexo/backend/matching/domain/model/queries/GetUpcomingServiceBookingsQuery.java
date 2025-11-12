package com.atg.autonexo.backend.matching.domain.model.queries;

import java.time.LocalDateTime;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;

/**
 * Query to get upcoming service bookings (for calendar view).
 */
public record GetUpcomingServiceBookingsQuery(
    WorkshopId workshopId,
    LocalDateTime fromDate,
    LocalDateTime toDate
) {
    public GetUpcomingServiceBookingsQuery {
        if (workshopId == null) {
            throw new IllegalArgumentException("WorkshopId cannot be null");
        }
        if (fromDate == null) {
            throw new IllegalArgumentException("FromDate cannot be null");
        }
        if (toDate == null) {
            throw new IllegalArgumentException("ToDate cannot be null");
        }
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("FromDate must be before or equal to ToDate");
        }
    }
}

