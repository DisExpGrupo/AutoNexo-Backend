package com.atg.autonexo.backend.workshop.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Resource for creating a new workshop
 */
public record CreateWorkshopResource(
    @NotNull(message = "Owner user ID is required")
    Long ownerUserId,
    
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    String name,
    
    @Size(max = 500, message = "Short description must not exceed 500 characters")
    String shortDescription,
    
    @Size(max = 300, message = "Legal name must not exceed 300 characters")
    String legalName,
    
    @Size(min = 11, max = 11, message = "RUC must be exactly 11 digits")
    String ruc
) {
}

