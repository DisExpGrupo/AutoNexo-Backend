package com.atg.autonexo.backend.vehicle.domain.model.commands;

/**
 * Command to confirm a workshop-created maintenance record.
 */
public record ConfirmMaintenanceCommand(
    Long maintenanceId
) {
    public ConfirmMaintenanceCommand {
        if (maintenanceId == null || maintenanceId <= 0) {
            throw new IllegalArgumentException("MaintenanceId must be valid");
        }
    }
}

