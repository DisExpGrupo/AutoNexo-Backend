package com.atg.autonexo.backend.vehicle.interfaces.rest.transform;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;
import com.atg.autonexo.backend.vehicle.domain.model.commands.CreateMaintenanceCommand;
import com.atg.autonexo.backend.vehicle.domain.model.commands.CreateVehicleCommand;
import com.atg.autonexo.backend.vehicle.domain.model.commands.UpdateVehicleMileageCommand;
import com.atg.autonexo.backend.vehicle.domain.model.valueobjects.LicensePlate;
import com.atg.autonexo.backend.vehicle.domain.model.valueobjects.Mileage;
import com.atg.autonexo.backend.vehicle.domain.model.valueobjects.VIN;
import com.atg.autonexo.backend.vehicle.interfaces.rest.resources.CreateMaintenanceResource;
import com.atg.autonexo.backend.vehicle.interfaces.rest.resources.CreateVehicleResource;
import com.atg.autonexo.backend.vehicle.interfaces.rest.resources.UpdateMileageResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembler for converting REST resources to domain commands.
 */
public class VehicleCommandFromResourceAssembler {
    
    public static CreateVehicleCommand toCommandFromResource(CreateVehicleResource resource) {
        return new CreateVehicleCommand(
            resource.brandId(),
            resource.model(),
            resource.year(),
            new LicensePlate(resource.licensePlate()),
            resource.vin() != null ? VIN.of(resource.vin()) : null,
            resource.color(),
            new Mileage(resource.initialMileage())
        );
    }
    
    public static UpdateVehicleMileageCommand toCommandFromResource(Long vehicleId, UpdateMileageResource resource) {
        return new UpdateVehicleMileageCommand(
            vehicleId,
            new Mileage(resource.mileage())
        );
    }
    
    public static CreateMaintenanceCommand toCommandFromResource(Long vehicleId, CreateMaintenanceResource resource) {
        List<CreateMaintenanceCommand.ServicePerformedData> services = new ArrayList<>();
        if (resource.services() != null) {
            for (CreateMaintenanceResource.ServicePerformedResource serviceResource : resource.services()) {
                services.add(new CreateMaintenanceCommand.ServicePerformedData(
                    ServiceCatalog.fromString(serviceResource.serviceType()),
                    serviceResource.description(),
                    serviceResource.cost()
                ));
            }
        }
        
        return new CreateMaintenanceCommand(
            vehicleId,
            resource.maintenanceDate(),
            new Mileage(resource.mileage()),
            resource.workshopId() != null ? new WorkshopId(resource.workshopId()) : null,
            resource.observations(),
            services
        );
    }
}

