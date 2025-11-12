package com.atg.autonexo.backend.matching.domain.model.queries;

/**
 * Query to get a service booking by ID.
 */
public record GetServiceBookingByIdQuery(
    Long serviceBookingId
) {
    public GetServiceBookingByIdQuery {
        if (serviceBookingId == null || serviceBookingId <= 0) {
            throw new IllegalArgumentException("ServiceBookingId must be valid");
        }
    }
}

