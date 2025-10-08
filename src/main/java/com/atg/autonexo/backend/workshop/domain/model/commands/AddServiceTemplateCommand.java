package com.atg.autonexo.backend.workshop.domain.model.commands;

import java.math.BigDecimal;

/**
 * Command to add a new service template to a workshop.
 * The service can be linked to a catalog entry or be a custom service.
 */
public record AddServiceTemplateCommand(
    Long workshopId,
    String code,
    String catalogService,  // Optional - null for custom services
    String customName,      // Required - workshop's name for the service
    String description,
    Integer estimatedDurationMinutes,
    BigDecimal basePriceAmount,
    String currency
) {
    public AddServiceTemplateCommand {
        if (workshopId == null || workshopId <= 0) {
            throw new IllegalArgumentException("Workshop ID cannot be null or negative.");
        }
        if (customName == null || customName.isBlank()) {
            throw new IllegalArgumentException("Custom name cannot be null or blank.");
        }
        if (estimatedDurationMinutes == null || estimatedDurationMinutes <= 0) {
            throw new IllegalArgumentException("Estimated duration must be positive.");
        }
        // catalogService can be null for custom services
    }
}
