package com.atg.autonexo.backend.matching.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Resource for marking service as completed.
 */
public record MarkCompletedResource(
    @NotNull(message = "Mileage is required")
    @Min(value = 0, message = "Mileage cannot be negative")
    Integer mileage,
    
    @NotEmpty(message = "At least one service is required")
    List<ServicePerformedResource> services,
    
    String observations,
    
    List<String> imageUrls,
    
    BigDecimal finalPriceAmount,
    
    String currency
) {
    public record ServicePerformedResource(
        @NotNull(message = "Service type is required")
        String serviceType,
        
        String description,
        
        @NotNull(message = "Cost is required")
        @Min(value = 0, message = "Cost cannot be negative")
        BigDecimal cost
    ) {}
}

