package com.atg.autonexo.backend.vehicle.domain.model.commands;

import com.atg.autonexo.backend.vehicle.domain.model.valueobjects.LicensePlate;
import com.atg.autonexo.backend.vehicle.domain.model.valueobjects.Mileage;
import com.atg.autonexo.backend.vehicle.domain.model.valueobjects.VIN;

/**
 * Command to create a new vehicle.
 */
public record CreateVehicleCommand(
    Long brandId,
    String model,
    Integer year,
    LicensePlate licensePlate,
    VIN vin,
    String color,
    Mileage initialMileage
) {
    public CreateVehicleCommand {
        if (brandId == null) {
            throw new IllegalArgumentException("Brand ID cannot be null");
        }
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Model cannot be null or empty");
        }
        if (year == null) {
            throw new IllegalArgumentException("Year cannot be null");
        }
        if (licensePlate == null) {
            throw new IllegalArgumentException("License plate cannot be null");
        }
        if (initialMileage == null) {
            throw new IllegalArgumentException("Initial mileage cannot be null");
        }
    }
}

