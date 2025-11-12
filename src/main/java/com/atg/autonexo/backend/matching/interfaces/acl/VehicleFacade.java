package com.atg.autonexo.backend.matching.interfaces.acl;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;

/**
 * Anti-Corruption Layer facade for Vehicle & Maintenance Bounded Context.
 * Provides access to vehicle information for validation purposes.
 */
public interface VehicleFacade {
    
    /**
     * Gets vehicle information.
     */
    VehicleInfo getVehicleInfo(Long vehicleId, UserId userId);
    
    /**
     * Checks if a user owns a vehicle.
     */
    boolean userOwnsVehicle(Long vehicleId, UserId userId);
    
    /**
     * Vehicle information record.
     */
    record VehicleInfo(
        Long id,
        String brand,
        String model,
        Integer year,
        UserId ownerId
    ) {}
}

