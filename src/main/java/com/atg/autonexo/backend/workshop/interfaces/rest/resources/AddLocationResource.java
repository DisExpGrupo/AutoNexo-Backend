package com.atg.autonexo.backend.workshop.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

/**
 * Resource for adding a location to a workshop
 */
public record AddLocationResource(
    @NotBlank(message = "Street is required")
    String street,
    
    @NotBlank(message = "City is required")
    String city,
    
    @NotBlank(message = "State is required")
    String state,
    
    @NotBlank(message = "Zip code is required")
    String zip,
    
    @NotBlank(message = "Country is required")
    String country,
    
    Double latitude,
    Double longitude
) {
}

