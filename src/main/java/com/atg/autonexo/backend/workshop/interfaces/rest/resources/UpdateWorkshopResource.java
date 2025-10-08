package com.atg.autonexo.backend.workshop.interfaces.rest.resources;

import jakarta.validation.constraints.Size;

/**
 * Resource for updating workshop basic information
 */
public record UpdateWorkshopResource(
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    String name,
    
    @Size(max = 500, message = "Short description must not exceed 500 characters")
    String shortDescription,
    
    @Size(max = 300, message = "Legal name must not exceed 300 characters")
    String legalName
) {
}

