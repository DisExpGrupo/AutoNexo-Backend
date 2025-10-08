package com.atg.autonexo.backend.workshop.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

/**
 * Resource for adding a staff member to a workshop
 */
public record AddStaffMemberResource(
    @NotNull(message = "User ID is required")
    Long userId,
    
    Long primaryLocationId
) {
}

