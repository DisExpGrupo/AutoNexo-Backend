package com.atg.autonexo.backend.vehicle.interfaces.rest.transform;

import com.atg.autonexo.backend.vehicle.domain.model.aggregates.Maintenance;
import com.atg.autonexo.backend.vehicle.domain.model.aggregates.Vehicle;
import com.atg.autonexo.backend.vehicle.domain.model.entities.ServicePerformed;
import com.atg.autonexo.backend.vehicle.interfaces.rest.resources.MaintenanceResource;
import com.atg.autonexo.backend.vehicle.interfaces.rest.resources.VehicleResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembler for converting domain entities to REST resources.
 */
public class VehicleResourceFromEntityAssembler {
    
    public static VehicleResource toResourceFromEntity(Vehicle vehicle) {
        return new VehicleResource(
            vehicle.getId(),
            vehicle.getBrand(),
            vehicle.getModel(),
            vehicle.getYear(),
            vehicle.getLicensePlate().value(),
            vehicle.getVin() != null ? vehicle.getVin().value() : null,
            vehicle.getColor(),
            vehicle.getCurrentMileage().value(),
            new ArrayList<>(vehicle.getImageUrls()),
            vehicle.isActive(),
            vehicle.getPrimaryOwnerId() != null ? vehicle.getPrimaryOwnerId().id() : null
        );
    }
    
    public static MaintenanceResource toResourceFromEntity(Maintenance maintenance) {
        List<MaintenanceResource.ServicePerformedResource> services = new ArrayList<>();
        if (maintenance.getServicesPerformed() != null) {
            for (ServicePerformed service : maintenance.getServicesPerformed()) {
                services.add(new MaintenanceResource.ServicePerformedResource(
                    service.getServiceType().name(),
                    service.getDescription(),
                    service.getCost()
                ));
            }
        }
        
        return new MaintenanceResource(
            maintenance.getId(),
            maintenance.getVehicleId(),
            maintenance.getMaintenanceDate(),
            maintenance.getMileage().value(),
            maintenance.getWorkshopId() != null ? maintenance.getWorkshopId().id() : null,
            maintenance.isCreatedByWorkshop(),
            maintenance.getStatus().name(),
            maintenance.getObservations(),
            new ArrayList<>(maintenance.getImageUrls()),
            services,
            maintenance.getTotalCost()
        );
    }
}

