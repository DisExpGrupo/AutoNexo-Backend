package com.atg.autonexo.backend.vehicle.domain.model.commands;

import com.atg.autonexo.backend.vehicle.domain.model.valueobjects.Mileage;

/**
 * Command to update vehicle mileage.
 */
public record UpdateVehicleMileageCommand(
    Long vehicleId,
    Mileage newMileage
) {
    public UpdateVehicleMileageCommand {
        if (vehicleId == null || vehicleId <= 0) {
            throw new IllegalArgumentException("VehicleId must be valid");
        }
        if (newMileage == null) {
            throw new IllegalArgumentException("Mileage cannot be null");
        }
    }
}

