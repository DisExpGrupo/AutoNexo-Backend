package com.atg.autonexo.backend.shared.interfaces.rest.resources.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Resource for creating a vehicle model.
 */
public record CreateVehicleModelResource(
    @NotNull(message = "Brand ID is required")
    Long brandId,
    
    @NotBlank(message = "Model name is required")
    String name,
    
    Integer startYear,
    
    Integer endYear
) {}

