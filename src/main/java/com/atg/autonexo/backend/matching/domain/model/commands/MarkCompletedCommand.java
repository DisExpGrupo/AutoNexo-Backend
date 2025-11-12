package com.atg.autonexo.backend.matching.domain.model.commands;

import java.math.BigDecimal;
import java.util.List;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;

/**
 * Command for a workshop to mark a service booking as completed.
 */
public record MarkCompletedCommand(
    Long serviceBookingId,
    WorkshopId workshopId,
    Integer mileage,
    List<ServicePerformedData> services,
    String observations,
    List<String> imageUrls,
    BigDecimal finalPriceAmount,
    String currency
) {
    public MarkCompletedCommand {
        if (serviceBookingId == null || serviceBookingId <= 0) {
            throw new IllegalArgumentException("ServiceBookingId must be valid");
        }
        if (workshopId == null) {
            throw new IllegalArgumentException("WorkshopId cannot be null");
        }
        if (mileage == null || mileage < 0) {
            throw new IllegalArgumentException("Mileage must be valid and non-negative");
        }
        if (services == null || services.isEmpty()) {
            throw new IllegalArgumentException("Services cannot be null or empty");
        }
    }
    
    public record ServicePerformedData(
        ServiceCatalog serviceType,
        String description,
        BigDecimal cost
    ) {}
}

