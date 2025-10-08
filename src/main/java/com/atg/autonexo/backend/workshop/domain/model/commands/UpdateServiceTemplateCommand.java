package com.atg.autonexo.backend.workshop.domain.model.commands;

import java.math.BigDecimal;

/**
 * Command to update a service template
 */
public record UpdateServiceTemplateCommand(
    Long workshopId,
    Long serviceTemplateId,
    String title,
    String description,
    Integer baseDurationMinutes,
    BigDecimal basePriceAmount,
    String currency
) {
    public UpdateServiceTemplateCommand {
        if (workshopId == null || workshopId <= 0) {
            throw new IllegalArgumentException("Workshop ID cannot be null or negative.");
        }
        if (serviceTemplateId == null || serviceTemplateId <= 0) {
            throw new IllegalArgumentException("Service template ID cannot be null or negative.");
        }
    }
}

