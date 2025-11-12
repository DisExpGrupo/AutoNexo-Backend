package com.atg.autonexo.backend.matching.application.internal.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.matching.domain.services.MatchingService;
import com.atg.autonexo.backend.matching.domain.services.ServiceCapabilityMappingService;
import com.atg.autonexo.backend.matching.interfaces.acl.WorkshopFacade;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.CapabilityTag;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.Coordinates;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionTier;

/**
 * Implementation of MatchingService.
 * Finds matching workshops for service requests based on distance, services, and rating.
 */
@Service
@Transactional(readOnly = true)
public class MatchingServiceImpl implements MatchingService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingServiceImpl.class);
    
    private final WorkshopFacade workshopFacade;
    private final ServiceCapabilityMappingService capabilityMappingService;
    
    // Earth's radius in kilometers
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    public MatchingServiceImpl(WorkshopFacade workshopFacade, ServiceCapabilityMappingService capabilityMappingService) {
        this.workshopFacade = workshopFacade;
        this.capabilityMappingService = capabilityMappingService;
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
            LOGGER.info("Workshop: {}", workshop);
            // Get workshop locations
            List<WorkshopFacade.LocationInfo> locations = workshopFacade.getWorkshopLocations(workshop.id());
            LOGGER.info("Locations: {}", locations);
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
            
            LOGGER.info("Min distance: {}", minDistance);
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
            
            // Permitir workshops sin servicios coincidentes, pero penalizar en el score
            // (útil para testing con datos limitados)
            
            // Get workshop capabilities and subscription tier
            Set<CapabilityTag> workshopCapabilities = workshopFacade.getWorkshopCapabilities(workshop.id());
            SubscriptionTier subscriptionTier = workshopFacade.getWorkshopSubscriptionTier(workshop.id());
            
            // Calculate match score with boosts
            double matchScore = calculateMatchScore(
                minDistance, 
                workshop.rating(), 
                matchingServices.size(), 
                requestedServices.size(),
                new HashSet<>(requestedServices),
                workshopCapabilities,
                subscriptionTier
            );
            
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
     * Calculates match score based on distance, rating, service matching, capabilities, and subscription tier.
     * Higher score = better match.
     */
    private double calculateMatchScore(double distanceKm, Double rating, int matchingServicesCount, int requestedServicesCount,
                                      Set<ServiceCatalog> requestedServices, Set<CapabilityTag> workshopCapabilities,
                                      SubscriptionTier subscriptionTier) {
        // Distance score: closer = higher score (inverse, normalized to 0-1)
        double maxDistance = 50.0; // Max search radius
        double distanceScore = 1.0 - (distanceKm / maxDistance);
        
        // Rating score: higher rating = higher score (normalized to 0-1, default 0.5 if null)
        double ratingScore = rating != null ? rating / 5.0 : 0.5;
        
        // Service matching score con penalización severa si no hay matches
        double serviceScore;
        if (matchingServicesCount == 0) {
            serviceScore = 0.05; // Penalización: solo 5% del score máximo
            LOGGER.debug("Workshop has NO matching services, applying heavy penalty (5% score)");
        } else {
            serviceScore = (double) matchingServicesCount / requestedServicesCount;
        }
        
        // Base weighted combination: distance 40%, rating 30%, services 30%
        double baseScore = (distanceScore * 0.4) + (ratingScore * 0.3) + (serviceScore * 0.3);
        
        // Capability tag boost: 10-20% boost if workshop has matching capabilities
        double capabilityBoost = 0.0;
        int matchingCapabilityCount = capabilityMappingService.countMatchingCapabilities(requestedServices, workshopCapabilities);
        if (matchingCapabilityCount > 0) {
            // 10% boost for first match, up to 20% for all services having matching capabilities
            double capabilityMatchRatio = (double) matchingCapabilityCount / requestedServices.size();
            capabilityBoost = baseScore * (0.10 + (0.10 * capabilityMatchRatio));
            LOGGER.debug("Workshop has {} matching capabilities, applying {:.1f}% boost", 
                matchingCapabilityCount, (capabilityBoost / baseScore) * 100);
        }
        
        // Subscription tier boost: PREMIUM +15%, BASIC +5%, TRIAL 0%
        double tierBoost = 0.0;
        if (subscriptionTier != null) {
            switch (subscriptionTier) {
                case PREMIUM:
                    tierBoost = baseScore * 0.15;
                    LOGGER.debug("PREMIUM tier, applying 15% boost");
                    break;
                case BASIC:
                    tierBoost = baseScore * 0.05;
                    LOGGER.debug("BASIC tier, applying 5% boost");
                    break;
                default:
                    // No boost for TRIAL/FREE tiers
                    break;
            }
        }
        
        double finalScore = baseScore + capabilityBoost + tierBoost;
        LOGGER.debug("Match score breakdown - Base: {:.3f}, Capability boost: {:.3f}, Tier boost: {:.3f}, Final: {:.3f}",
            baseScore, capabilityBoost, tierBoost, finalScore);
        
        return finalScore;
    }
}

