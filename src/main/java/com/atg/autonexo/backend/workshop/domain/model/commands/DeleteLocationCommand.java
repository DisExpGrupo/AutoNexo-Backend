package com.atg.autonexo.backend.workshop.domain.model.commands;

/**
 * Command to delete/deactivate a location from a workshop.
 */
public record DeleteLocationCommand(
    Long workshopId,
    Long locationId
) {
    public DeleteLocationCommand {
        if (workshopId == null || workshopId <= 0) {
            throw new IllegalArgumentException("WorkshopId must be valid");
        }
        if (locationId == null || locationId <= 0) {
            throw new IllegalArgumentException("LocationId must be valid");
        }
    }
}

