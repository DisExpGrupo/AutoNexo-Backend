package com.atg.autonexo.backend.vehicle.domain.model.commands;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;
import com.atg.autonexo.backend.vehicle.domain.model.valueobjects.Mileage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Command to create a manual maintenance record.
 */
public record CreateMaintenanceCommand(
    Long vehicleId,
    LocalDate maintenanceDate,
    Mileage mileage,
    WorkshopId workshopId, // Optional - null for external shops
    String observations,
    List<ServicePerformedData> services
) {
    public record ServicePerformedData(
        ServiceCatalog serviceType,
        String description,
        BigDecimal cost
    ) {}
    
    public CreateMaintenanceCommand {
        if (vehicleId == null || vehicleId <= 0) {
            throw new IllegalArgumentException("VehicleId must be valid");
        }
        if (maintenanceDate == null) {
            throw new IllegalArgumentException("MaintenanceDate cannot be null");
        }
        if (mileage == null) {
            throw new IllegalArgumentException("Mileage cannot be null");
        }
    }
}

