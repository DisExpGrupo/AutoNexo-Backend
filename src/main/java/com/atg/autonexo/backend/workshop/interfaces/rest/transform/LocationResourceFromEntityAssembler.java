package com.atg.autonexo.backend.workshop.interfaces.rest.transform;

import com.atg.autonexo.backend.workshop.domain.model.entities.Location;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.LocationResource;

/**
 * Assembler for converting Location entity to LocationResource
 */
public class LocationResourceFromEntityAssembler {
    
    /**
     * Converts a Location entity to a LocationResource
     * @param location the Location entity from the domain
     * @return LocationResource for REST response
     */
    public static LocationResource toResourceFromEntity(Location location) {
        Double latitude = null;
        Double longitude = null;
        
        if (location.getCoordinates() != null) {
            latitude = location.getCoordinates().latitude();
            longitude = location.getCoordinates().longitude();
        }
        
        return new LocationResource(
            location.getId(),
            location.getAddress().street(),
            location.getAddress().city(),
            location.getAddress().state(),
            location.getAddress().zip(),
            location.getAddress().country(),
            latitude,
            longitude,
            location.isActive(),
            location.getCreatedAt(),
            location.getUpdatedAt()
        );
    }
}

