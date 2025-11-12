package com.atg.autonexo.backend.matching.application.internal.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.matching.domain.services.MatchingService;
import com.atg.autonexo.backend.matching.interfaces.acl.WorkshopFacade;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.Coordinates;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;

/**
 * Implementation of MatchingService.
 * Finds matching workshops for service requests based on distance, services, and rating.
 */
@Service
@Transactional(readOnly = true)
public class MatchingServiceImpl implements MatchingService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingServiceImpl.class);
    
    private final WorkshopFacade workshopFacade;
    
    // Earth's radius in kilometers
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    public MatchingServiceImpl(WorkshopFacade workshopFacade) {
        this.workshopFacade = workshopFacade;
    }
    
    @Override
    public List<WorkshopMatchResult> findMatchingWorkshops(
            Coordinates userLocation,
            Integer searchRadiusKm,
            List<ServiceCatalog> requestedServices,
            Optional<Double> minRating) {
        
        LOGGER.info("Finding matching workshops for location ({}, {}), radius {} km, services: {}", 
            userLocation.latitude(), userLocation.longitude(), searchRadiusKm, requestedServices);
        
        // Get all active workshops with their locations
        List<WorkshopFacade.WorkshopInfo> allWorkshops = workshopFacade.getAllActiveWorkshops();
        
        List<WorkshopMatchResult> matches = new ArrayList<>();
        
        for (WorkshopFacade.WorkshopInfo workshop : allWorkshops) {
            // Get workshop locations
            List<WorkshopFacade.LocationInfo> locations = workshopFacade.getWorkshopLocations(workshop.id());
            
            // Find closest location
            Double minDistance = null;
            for (WorkshopFacade.LocationInfo location : locations) {
                if (location.coordinates() != null && location.active()) {
                    double distance = calculateDistance(userLocation, location.coordinates());
                    if (minDistance == null || distance < minDistance) {
                        minDistance = distance;
                    }
                }
            }
            
            // Skip if no valid location or outside radius
            if (minDistance == null || minDistance > searchRadiusKm) {
                continue;
            }
            
            // Filter by rating if specified
            if (minRating.isPresent() && (workshop.rating() == null || workshop.rating() < minRating.get())) {
                continue;
            }
            
            // Get workshop services
            List<ServiceCatalog> workshopServices = workshopFacade.getWorkshopServices(workshop.id());
            
            // Find matching services
            List<ServiceCatalog> matchingServices = requestedServices.stream()
                .filter(workshopServices::contains)
                .collect(Collectors.toList());
            
            // Skip if no matching services
            if (matchingServices.isEmpty()) {
                continue;
            }
            
            // Calculate match score
            double matchScore = calculateMatchScore(minDistance, workshop.rating(), matchingServices.size(), requestedServices.size());
            
            matches.add(new WorkshopMatchResult(
                workshop.id(),
                workshop.name(),
                minDistance,
                workshop.rating(),
                matchingServices,
                matchScore
            ));
        }
        
        // Sort by match score descending
        matches.sort((a, b) -> Double.compare(b.matchScore(), a.matchScore()));
        
        // Return top 20 results
        int maxResults = Math.min(20, matches.size());
        List<WorkshopMatchResult> topMatches = matches.subList(0, maxResults);
        
        LOGGER.info("Found {} matching workshops", topMatches.size());
        return topMatches;
    }
    
    /**
     * Calculates distance between two coordinates using Haversine formula.
     */
    private double calculateDistance(Coordinates coord1, Coordinates coord2) {
        double lat1 = Math.toRadians(coord1.latitude());
        double lon1 = Math.toRadians(coord1.longitude());
        double lat2 = Math.toRadians(coord2.latitude());
        double lon2 = Math.toRadians(coord2.longitude());
        
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
    
    /**
     * Calculates match score based on distance, rating, and service matching.
     * Higher score = better match.
     */
    private double calculateMatchScore(double distanceKm, Double rating, int matchingServicesCount, int requestedServicesCount) {
        // Distance score: closer = higher score (inverse, normalized to 0-1)
        double maxDistance = 50.0; // Max search radius
        double distanceScore = 1.0 - (distanceKm / maxDistance);
        
        // Rating score: higher rating = higher score (normalized to 0-1, default 0.5 if null)
        double ratingScore = rating != null ? rating / 5.0 : 0.5;
        
        // Service matching score: more matching services = higher score
        double serviceScore = (double) matchingServicesCount / requestedServicesCount;
        
        // Weighted combination: distance 40%, rating 30%, services 30%
        return (distanceScore * 0.4) + (ratingScore * 0.3) + (serviceScore * 0.3);
    }
}

