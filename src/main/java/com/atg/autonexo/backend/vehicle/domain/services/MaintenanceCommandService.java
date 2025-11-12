package com.atg.autonexo.backend.vehicle.domain.services;

import com.atg.autonexo.backend.vehicle.domain.model.aggregates.Maintenance;
import com.atg.autonexo.backend.vehicle.domain.model.commands.ConfirmMaintenanceCommand;
import com.atg.autonexo.backend.vehicle.domain.model.commands.CreateMaintenanceCommand;
import com.atg.autonexo.backend.vehicle.domain.model.commands.RejectMaintenanceCommand;

/**
 * Domain service interface for maintenance command operations.
 */
public interface MaintenanceCommandService {
    
    Maintenance handle(CreateMaintenanceCommand command);
    
    void handle(ConfirmMaintenanceCommand command);
    
    void handle(RejectMaintenanceCommand command);
}

