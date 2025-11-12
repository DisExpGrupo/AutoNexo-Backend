package com.atg.autonexo.backend.workshop.domain.model.commands;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.Address;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.Coordinates;

/**
 * Command to update a location in a workshop.
 */
public record UpdateLocationCommand(
    Long workshopId,
    Long locationId,
    String name,
    Address address,
    Coordinates coordinates,
    boolean isPrimary
) {
    public UpdateLocationCommand {
        if (workshopId == null || workshopId <= 0) {
            throw new IllegalArgumentException("WorkshopId must be valid");
        }
        if (locationId == null || locationId <= 0) {
            throw new IllegalArgumentException("LocationId must be valid");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Location name cannot be null or blank");
        }
    }
}

