package com.atg.autonexo.backend.vehicle.domain.model.queries;

/**
 * Query to get a vehicle by ID (with authorization check).
 */
public record GetVehicleByIdQuery(
    Long vehicleId,
    Long userId
) {
    public GetVehicleByIdQuery {
        if (vehicleId == null || vehicleId <= 0) {
            throw new IllegalArgumentException("VehicleId must be valid");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("UserId must be valid");
        }
    }
}

