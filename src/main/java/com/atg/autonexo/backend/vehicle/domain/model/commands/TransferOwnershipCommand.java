package com.atg.autonexo.backend.vehicle.domain.model.commands;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;

/**
 * Command to transfer vehicle ownership to a new user.
 */
public record TransferOwnershipCommand(
    Long vehicleId,
    UserId newOwnerId
) {
    public TransferOwnershipCommand {
        if (vehicleId == null || vehicleId <= 0) {
            throw new IllegalArgumentException("VehicleId must be valid");
        }
        if (newOwnerId == null) {
            throw new IllegalArgumentException("NewOwnerId cannot be null");
        }
    }
}

