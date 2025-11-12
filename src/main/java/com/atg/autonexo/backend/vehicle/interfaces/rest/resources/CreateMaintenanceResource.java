package com.atg.autonexo.backend.vehicle.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Resource for creating a maintenance record.
 */
public record CreateMaintenanceResource(
    @NotNull(message = "Maintenance date is required")
    LocalDate maintenanceDate,
    
    @NotNull(message = "Mileage is required")
    @Min(value = 0, message = "Mileage cannot be negative")
    Integer mileage,
    
    Long workshopId, // Optional
    
    @Size(max = 1000, message = "Observations must not exceed 1000 characters")
    String observations,
    
    List<ServicePerformedResource> services
) {
    public record ServicePerformedResource(
        @NotNull(message = "Service type is required")
        String serviceType,
        
        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,
        
        @NotNull(message = "Cost is required")
        @Min(value = 0, message = "Cost cannot be negative")
        BigDecimal cost
    ) {}
}

