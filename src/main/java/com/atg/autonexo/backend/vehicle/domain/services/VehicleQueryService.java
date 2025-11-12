package com.atg.autonexo.backend.vehicle.domain.services;

import com.atg.autonexo.backend.vehicle.domain.model.aggregates.Vehicle;
import com.atg.autonexo.backend.vehicle.domain.model.queries.GetUserVehiclesQuery;
import com.atg.autonexo.backend.vehicle.domain.model.queries.GetVehicleByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Domain service interface for vehicle query operations.
 */
public interface VehicleQueryService {
    
    List<Vehicle> handle(GetUserVehiclesQuery query);
    
    Optional<Vehicle> handle(GetVehicleByIdQuery query);
}

