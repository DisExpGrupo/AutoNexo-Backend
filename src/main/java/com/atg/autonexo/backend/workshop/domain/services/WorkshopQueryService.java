package com.atg.autonexo.backend.workshop.domain.services;

import com.atg.autonexo.backend.workshop.domain.model.aggregates.Workshop;
import com.atg.autonexo.backend.workshop.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

/**
 * Domain service interface for Workshop query operations.
 * Follows CQRS pattern for read operations.
 */
public interface WorkshopQueryService {
    
    /**
     * Gets a workshop by ID
     */
    Optional<Workshop> handle(GetWorkshopByIdQuery query);
    
    /**
     * Gets a workshop by owner user ID
     */
    Optional<Workshop> handle(GetWorkshopByOwnerQuery query);
    
    /**
     * Gets all active workshops
     */
    List<Workshop> handle(GetAllWorkshopsQuery query);
    
    /**
     * Gets workshops by capability tag
     */
    List<Workshop> handle(GetWorkshopsByCapabilityTagQuery query);
}

