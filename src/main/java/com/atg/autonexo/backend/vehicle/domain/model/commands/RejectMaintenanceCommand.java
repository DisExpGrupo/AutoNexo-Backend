package com.atg.autonexo.backend.vehicle.domain.model.commands;

/**
 * Command to reject a workshop-created maintenance record.
 */
public record RejectMaintenanceCommand(
    Long maintenanceId
) {
    public RejectMaintenanceCommand {
        if (maintenanceId == null || maintenanceId <= 0) {
            throw new IllegalArgumentException("MaintenanceId must be valid");
        }
    }
}

