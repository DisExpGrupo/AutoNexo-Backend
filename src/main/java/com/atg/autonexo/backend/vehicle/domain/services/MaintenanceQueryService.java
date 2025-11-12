package com.atg.autonexo.backend.vehicle.domain.services;

import com.atg.autonexo.backend.vehicle.domain.model.aggregates.Maintenance;
import com.atg.autonexo.backend.vehicle.domain.model.queries.GetMaintenanceByIdQuery;
import com.atg.autonexo.backend.vehicle.domain.model.queries.GetPendingMaintenancesQuery;
import com.atg.autonexo.backend.vehicle.domain.model.queries.GetVehicleMaintenanceHistoryQuery;

import java.util.List;
import java.util.Optional;

/**
 * Domain service interface for maintenance query operations.
 */
public interface MaintenanceQueryService {
    
    List<Maintenance> handle(GetVehicleMaintenanceHistoryQuery query);
    
    Optional<Maintenance> handle(GetMaintenanceByIdQuery query);
    
    List<Maintenance> handle(GetPendingMaintenancesQuery query);
}

