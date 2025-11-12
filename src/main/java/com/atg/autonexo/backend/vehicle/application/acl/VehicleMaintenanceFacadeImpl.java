package com.atg.autonexo.backend.vehicle.application.acl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.vehicle.domain.exceptions.VehicleNotFoundException;
import com.atg.autonexo.backend.vehicle.domain.model.aggregates.Maintenance;
import com.atg.autonexo.backend.vehicle.domain.model.valueobjects.Mileage;
import com.atg.autonexo.backend.vehicle.infrastructure.persistence.jpa.repositories.MaintenanceRepository;
import com.atg.autonexo.backend.vehicle.infrastructure.persistence.jpa.repositories.VehicleRepository;
import com.atg.autonexo.backend.vehicle.interfaces.acl.VehicleMaintenanceFacade;

/**
 * Implementation of VehicleMaintenanceFacade.
 * Provides ACL for other bounded contexts to create maintenance records.
 */
@Service
@Transactional
public class VehicleMaintenanceFacadeImpl implements VehicleMaintenanceFacade {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleMaintenanceFacadeImpl.class);
    
    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;
    
    public VehicleMaintenanceFacadeImpl(
            MaintenanceRepository maintenanceRepository,
            VehicleRepository vehicleRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.vehicleRepository = vehicleRepository;
    }
    
    @Override
    public Long createMaintenanceFromCompletedService(
            Long vehicleId,
            com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId workshopId,
            java.time.LocalDate maintenanceDate,
            Integer mileage,
            List<ServicePerformedData> services,
            String observations,
            List<String> imageUrls) {
        
        LOGGER.info("Creating maintenance from completed service for vehicle {} by workshop {}", 
            vehicleId, workshopId.id());
        
        // Verify vehicle exists
        vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
        
        // Create maintenance using workshop factory method (PENDING_CONFIRMATION)
        Maintenance maintenance = Maintenance.createFromWorkshop(
            vehicleId,
            maintenanceDate,
            new Mileage(mileage),
            workshopId,
            observations
        );
        
        // Add services
        if (services != null) {
            for (ServicePerformedData serviceData : services) {
                maintenance.addService(serviceData.serviceType(), serviceData.description(), serviceData.cost());
            }
        }
        
        // Add images if provided
        if (imageUrls != null) {
            for (String imageUrl : imageUrls) {
                maintenance.addImage(imageUrl);
            }
        }
        
        // Save maintenance (services are already added via addService method)
        Maintenance savedMaintenance = maintenanceRepository.save(maintenance);
        
        // Update maintenance ID for all services
        savedMaintenance.updateServicesMaintenanceId();
        savedMaintenance = maintenanceRepository.save(savedMaintenance);
        
        LOGGER.info("Maintenance created successfully with ID: {}", savedMaintenance.getId());
        return savedMaintenance.getId();
    }
}

