package com.atg.autonexo.backend.vehicle.domain.model.queries;

/**
 * Query to get pending maintenance confirmations for a user's vehicles.
 */
public record GetPendingMaintenancesQuery(
    Long userId
) {
    public GetPendingMaintenancesQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("UserId must be valid");
        }
    }
}

