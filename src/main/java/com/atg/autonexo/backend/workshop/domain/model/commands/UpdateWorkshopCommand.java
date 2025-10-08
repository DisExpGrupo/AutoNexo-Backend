package com.atg.autonexo.backend.workshop.domain.model.commands;

/**
 * Command to update workshop basic information
 */
public record UpdateWorkshopCommand(
    Long workshopId,
    String name,
    String shortDescription,
    String legalName
) {
    public UpdateWorkshopCommand {
        if (workshopId == null || workshopId <= 0) {
            throw new IllegalArgumentException("Workshop ID cannot be null or negative.");
        }
    }
}

