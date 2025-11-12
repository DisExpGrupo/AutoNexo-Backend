package com.atg.autonexo.backend.vehicle.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Resource for maintenance representation.
 */
public record MaintenanceResource(
    Long id,
    Long vehicleId,
    LocalDate maintenanceDate,
    Integer mileage,
    Long workshopId,
    boolean createdByWorkshop,
    String status,
    String observations,
    List<String> imageUrls,
    List<ServicePerformedResource> services,
    BigDecimal totalCost
) {
    public record ServicePerformedResource(
        String serviceType,
        String description,
        BigDecimal cost
    ) {}
}

