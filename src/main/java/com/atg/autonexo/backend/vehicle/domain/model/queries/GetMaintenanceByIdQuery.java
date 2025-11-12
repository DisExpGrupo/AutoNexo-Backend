package com.atg.autonexo.backend.vehicle.domain.model.queries;

/**
 * Query to get a maintenance record by ID.
 */
public record GetMaintenanceByIdQuery(
    Long maintenanceId,
    Long userId
) {
    public GetMaintenanceByIdQuery {
        if (maintenanceId == null || maintenanceId <= 0) {
            throw new IllegalArgumentException("MaintenanceId must be valid");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("UserId must be valid");
        }
    }
}

