package com.atg.autonexo.backend.matching.domain.model.queries;

import com.atg.autonexo.backend.matching.domain.model.valueobjects.ServiceBookingStatus;

/**
 * Query to get service bookings for a user.
 */
public record GetUserServiceBookingsQuery(
    Long userId,
    ServiceBookingStatus status
) {
    public GetUserServiceBookingsQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("UserId must be valid");
        }
        // status can be null to get all bookings
    }
}

