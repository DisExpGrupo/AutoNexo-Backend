package com.atg.autonexo.backend.matching.interfaces.rest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atg.autonexo.backend.matching.application.internal.services.MatchingServiceImpl;
import com.atg.autonexo.backend.matching.domain.services.MatchingService;
import com.atg.autonexo.backend.matching.interfaces.rest.resources.WorkshopMatchResultResource;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.Coordinates;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;

/**
 * REST Controller for matching operations.
 */
@RestController
@RequestMapping("/api/matching")
public class MatchingController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingController.class);
    
    private final MatchingService matchingService;
    
    public MatchingController(MatchingServiceImpl matchingService) {
        this.matchingService = matchingService;
    }
    
    @GetMapping("/workshops")
    public ResponseEntity<?> findMatchingWorkshops(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Integer radiusKm,
            @RequestParam(required = false) String services,
            @RequestParam(required = false) Double minRating) {
        try {
            Coordinates userLocation = new Coordinates(latitude, longitude);
            
            List<ServiceCatalog> requestedServices = List.of();
            if (services != null && !services.isBlank()) {
                requestedServices = java.util.Arrays.stream(services.split(","))
                    .map(String::trim)
                    .map(ServiceCatalog::fromString)
                    .collect(Collectors.toList());
            }
            
            Optional<Double> minRatingOpt = minRating != null ? Optional.of(minRating) : Optional.empty();
            
            List<MatchingService.WorkshopMatchResult> matches = matchingService.findMatchingWorkshops(
                userLocation,
                radiusKm,
                requestedServices,
                minRatingOpt
            );
            
            List<WorkshopMatchResultResource> resources = matches.stream()
                .map(m -> new WorkshopMatchResultResource(
                    m.workshopId().id(),
                    m.workshopName(),
                    m.distanceKm(),
                    m.rating(),
                    m.matchingServices().stream().map(Enum::name).collect(Collectors.toList()),
                    m.matchScore()
                ))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(resources);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error finding matching workshops", e);
            return ResponseEntity.internalServerError()
                .body("An error occurred while finding matching workshops");
        }
    }
}

