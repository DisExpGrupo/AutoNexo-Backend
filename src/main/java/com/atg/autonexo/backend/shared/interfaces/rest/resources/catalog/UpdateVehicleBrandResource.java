package com.atg.autonexo.backend.shared.interfaces.rest.resources.catalog;

import jakarta.validation.constraints.NotBlank;

/**
 * Resource for updating a vehicle brand.
 */
public record UpdateVehicleBrandResource(
    @NotBlank(message = "Brand name is required")
    String name,
    
    String logoUrl,
    
    String country,
    
    boolean popular
) {}

