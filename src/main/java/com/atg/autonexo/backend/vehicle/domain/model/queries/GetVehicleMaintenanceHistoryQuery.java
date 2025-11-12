package com.atg.autonexo.backend.vehicle.domain.model.queries;

/**
 * Query to get maintenance history for a vehicle.
 */
public record GetVehicleMaintenanceHistoryQuery(
    Long vehicleId,
    Long userId
) {
    public GetVehicleMaintenanceHistoryQuery {
        if (vehicleId == null || vehicleId <= 0) {
            throw new IllegalArgumentException("VehicleId must be valid");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("UserId must be valid");
        }
    }
}

