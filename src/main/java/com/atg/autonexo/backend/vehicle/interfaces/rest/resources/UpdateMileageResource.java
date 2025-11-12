package com.atg.autonexo.backend.vehicle.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Resource for updating vehicle mileage.
 */
public record UpdateMileageResource(
    @NotNull(message = "Mileage is required")
    @Min(value = 0, message = "Mileage cannot be negative")
    Integer mileage
) {}

