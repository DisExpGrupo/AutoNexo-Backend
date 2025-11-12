package com.atg.autonexo.backend.vehicle.interfaces.acl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;

/**
 * Anti-Corruption Layer facade for Vehicle & Maintenance context.
 * This interface allows other bounded contexts (like Matching & Booking) 
 * to create maintenance records when services are completed.
 */
public interface VehicleMaintenanceFacade {
    
    /**
     * Creates a maintenance record from a completed service.
     * This is called by Matching & Booking context when a service is completed.
     * 
     * @param vehicleId the vehicle ID
     * @param workshopId the workshop that performed the service
     * @param maintenanceDate the date the service was performed
     * @param mileage the vehicle mileage at time of service
     * @param services list of services performed with costs
     * @param observations optional observations
     * @param imageUrls optional image URLs (before/after photos)
     * @return the created maintenance ID
     * @throws IllegalArgumentException if vehicle not found or invalid data
     */
    Long createMaintenanceFromCompletedService(
        Long vehicleId,
        WorkshopId workshopId,
        LocalDate maintenanceDate,
        Integer mileage,
        List<ServicePerformedData> services,
        String observations,
        List<String> imageUrls
    );
    
    /**
     * Data structure for services performed.
     */
    record ServicePerformedData(
        ServiceCatalog serviceType,
        String description,
        BigDecimal cost
    ) {}
}

