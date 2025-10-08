package com.atg.autonexo.backend.workshop.interfaces.rest.resources;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Resource for adding a service template to a workshop.
 * The service can be linked to a catalog entry (optional) or be a custom service.
 */
public record AddServiceTemplateResource(
    @Size(min = 3, max = 20, message = "Code must be between 3 and 20 characters")
    String code,
    
    /**
     * Optional link to ServiceCatalog.
     * Use enum name (e.g., "OIL_CHANGE", "BRAKE_PAD_REPLACEMENT").
     * Leave null for custom services not in catalog.
     */
    @Size(max = 100, message = "Catalog service must not exceed 100 characters")
    String catalogService,
    
    /**
     * Workshop's custom name for this service (required).
     * This is the name that will be displayed to customers.
     */
    @NotBlank(message = "Custom name is required")
    @Size(min = 3, max = 200, message = "Custom name must be between 3 and 200 characters")
    String customName,
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    String description,
    
    @NotNull(message = "Estimated duration is required")
    @Min(value = 1, message = "Estimated duration must be at least 1 minute")
    Integer estimatedDurationMinutes,
    
    BigDecimal basePriceAmount,
    
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
    String currency
) {
}
