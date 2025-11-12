package com.atg.autonexo.backend.vehicle.domain.model.commands;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;

/**
 * Command to add an authorized user to a vehicle.
 */
public record AddAuthorizedUserCommand(
    Long vehicleId,
    UserId authorizedUserId
) {
    public AddAuthorizedUserCommand {
        if (vehicleId == null || vehicleId <= 0) {
            throw new IllegalArgumentException("VehicleId must be valid");
        }
        if (authorizedUserId == null) {
            throw new IllegalArgumentException("AuthorizedUserId cannot be null");
        }
    }
}

