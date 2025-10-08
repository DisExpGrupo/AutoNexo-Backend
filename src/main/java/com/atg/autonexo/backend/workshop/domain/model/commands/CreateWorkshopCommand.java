package com.atg.autonexo.backend.workshop.domain.model.commands;

/**
 * Command to create a new workshop
 */
public record CreateWorkshopCommand(
    Long ownerUserId,
    String name,
    String shortDescription,
    String legalName,
    String ruc
) {
    public CreateWorkshopCommand {
        if (ownerUserId == null || ownerUserId <= 0) {
            throw new IllegalArgumentException("Owner user ID cannot be null or negative.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank.");
        }
    }
}

