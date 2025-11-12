package com.atg.autonexo.backend.matching.domain.services;

import java.util.List;
import java.util.Optional;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.Coordinates;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;

/**
 * Domain service for matching workshops to service requests.
 */
public interface MatchingService {
    
    /**
     * Finds matching workshops for a service request.
     * 
     * @param userLocation the user's location
     * @param searchRadiusKm the search radius in kilometers
     * @param requestedServices the services requested
     * @param minRating optional minimum rating filter
     * @return list of matching workshops ordered by match score
     */
    List<WorkshopMatchResult> findMatchingWorkshops(
        Coordinates userLocation,
        Integer searchRadiusKm,
        List<ServiceCatalog> requestedServices,
        Optional<Double> minRating
    );
    
    /**
     * Result of workshop matching.
     */
    record WorkshopMatchResult(
        com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId workshopId,
        String workshopName,
        Double distanceKm,
        Double rating,
        List<ServiceCatalog> matchingServices,
        Double matchScore
    ) {}
}

