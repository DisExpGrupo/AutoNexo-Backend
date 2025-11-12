package com.atg.autonexo.backend.matching.interfaces.acl;

import java.util.List;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.Coordinates;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;

/**
 * Anti-Corruption Layer facade for Workshop Bounded Context.
 * Provides access to workshop information for matching purposes.
 */
public interface WorkshopFacade {
    
    /**
     * Gets basic information about a workshop.
     */
    WorkshopInfo getWorkshopInfo(WorkshopId workshopId);
    
    /**
     * Gets all locations for a workshop.
     */
    List<LocationInfo> getWorkshopLocations(WorkshopId workshopId);
    
    /**
     * Gets all services offered by a workshop (from ServiceTemplates linked to ServiceCatalog).
     */
    List<ServiceCatalog> getWorkshopServices(WorkshopId workshopId);
    
    /**
     * Gets the rating/trust score of a workshop.
     */
    Double getWorkshopRating(WorkshopId workshopId);
    
    /**
     * Gets all active workshops.
     */
    List<WorkshopInfo> getAllActiveWorkshops();
    
    /**
     * Workshop information record.
     */
    record WorkshopInfo(
        WorkshopId id,
        String name,
        Coordinates primaryLocation,
        Double rating,
        boolean active
    ) {}
    
    /**
     * Location information record.
     */
    record LocationInfo(
        Long locationId,
        Coordinates coordinates,
        boolean active
    ) {}
}

