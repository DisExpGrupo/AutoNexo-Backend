package com.atg.autonexo.backend.vehicle.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Resource for creating a new vehicle.
 */
public record CreateVehicleResource(
    @NotNull(message = "Brand ID is required")
    Long brandId,
    
    @NotBlank(message = "Model is required")
    @Size(max = 100, message = "Model must not exceed 100 characters")
    String model,
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be at least 1900")
    Integer year,
    
    @NotBlank(message = "License plate is required")
    @Size(max = 20, message = "License plate must not exceed 20 characters")
    String licensePlate,
    
    @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters")
    String vin,
    
    @Size(max = 50, message = "Color must not exceed 50 characters")
    String color,
    
    @NotNull(message = "Initial mileage is required")
    @Min(value = 0, message = "Mileage cannot be negative")
    Integer initialMileage
) {}

