package com.atg.autonexo.backend.shared.interfaces.rest.resources.catalog;

import jakarta.validation.constraints.NotBlank;

/**
 * Resource for creating a vehicle brand.
 */
public record CreateVehicleBrandResource(
    @NotBlank(message = "Brand name is required")
    String name,
    
    String logoUrl,
    
    String country,
    
    boolean popular
) {}

