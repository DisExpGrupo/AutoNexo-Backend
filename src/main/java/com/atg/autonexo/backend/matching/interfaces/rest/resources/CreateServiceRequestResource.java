package com.atg.autonexo.backend.matching.interfaces.rest.resources;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Resource for creating a service request.
 */
public record CreateServiceRequestResource(
    @NotNull(message = "Vehicle ID is required")
    Long vehicleId,
    
    @NotEmpty(message = "At least one service is required")
    List<@NotBlank String> requestedServices,
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    String description,
    
    @NotNull(message = "Latitude is required")
    Double latitude,
    
    @NotNull(message = "Longitude is required")
    Double longitude,
    
    @NotNull(message = "Search radius is required")
    @Min(value = 1, message = "Search radius must be at least 1 km")
    @Max(value = 50, message = "Search radius must be at most 50 km")
    Integer searchRadiusKm
) {}

