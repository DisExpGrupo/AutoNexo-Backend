package com.atg.autonexo.backend.vehicle.domain.services;

import com.atg.autonexo.backend.vehicle.domain.model.aggregates.Vehicle;
import com.atg.autonexo.backend.vehicle.domain.model.commands.AddAuthorizedUserCommand;
import com.atg.autonexo.backend.vehicle.domain.model.commands.CreateVehicleCommand;
import com.atg.autonexo.backend.vehicle.domain.model.commands.RemoveAuthorizedUserCommand;
import com.atg.autonexo.backend.vehicle.domain.model.commands.TransferOwnershipCommand;
import com.atg.autonexo.backend.vehicle.domain.model.commands.UpdateVehicleMileageCommand;

/**
 * Domain service interface for vehicle command operations.
 */
public interface VehicleCommandService {
    
    Vehicle handle(CreateVehicleCommand command);
    
    Vehicle handle(UpdateVehicleMileageCommand command);
    
    void handle(AddAuthorizedUserCommand command);
    
    void handle(RemoveAuthorizedUserCommand command);
    
    void handle(TransferOwnershipCommand command);
    
    void deactivateVehicle(Long vehicleId, Long userId);
}

