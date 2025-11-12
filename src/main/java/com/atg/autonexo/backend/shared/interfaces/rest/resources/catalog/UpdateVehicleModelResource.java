package com.atg.autonexo.backend.shared.interfaces.rest.resources.catalog;

import jakarta.validation.constraints.NotBlank;

/**
 * Resource for updating a vehicle model.
 */
public record UpdateVehicleModelResource(
    @NotBlank(message = "Model name is required")
    String name,
    
    Integer startYear,
    
    Integer endYear
) {}

