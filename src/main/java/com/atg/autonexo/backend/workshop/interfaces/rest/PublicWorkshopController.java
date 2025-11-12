package com.atg.autonexo.backend.workshop.interfaces.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.CapabilityTag;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCategory;
import com.atg.autonexo.backend.workshop.domain.model.aggregates.Workshop;
import com.atg.autonexo.backend.workshop.infrastructure.persistence.jpa.repositories.WorkshopRepository;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.LocationResource;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.PublicWorkshopProfile;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.ServiceTemplateResource;
import com.atg.autonexo.backend.workshop.interfaces.rest.resources.WorkshopSearchResult;
import com.atg.autonexo.backend.workshop.interfaces.rest.transform.LocationResourceFromEntityAssembler;
import com.atg.autonexo.backend.workshop.interfaces.rest.transform.ServiceTemplateResourceFromEntityAssembler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Public REST controller for workshop search and catalog endpoints.
 * These endpoints are accessible without authentication.
 */
@RestController
@RequestMapping("/api/v1/workshops")
@RequiredArgsConstructor
@Tag(name = "Public Workshops", description = "Public workshop search and catalog endpoints")
public class PublicWorkshopController {
    
    private final WorkshopRepository workshopRepository;
    
    /**
     * Search workshops with various criteria.
     * 
     * @param latitude User's latitude for distance calculation
     * @param longitude User's longitude for distance calculation
     * @param radiusKm Search radius in kilometers (default: 50)
     * @param services Comma-separated list of services required
     * @param tags Comma-separated list of capability tags
     * @param minRating Minimum trust score rating
     * @return List of matching workshops
     */
    @GetMapping("/search")
    @Operation(summary = "Search workshops", description = "Search for workshops based on location, services, tags, and rating")
    public ResponseEntity<List<WorkshopSearchResult>> searchWorkshops(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(defaultValue = "50") Integer radiusKm,
            @RequestParam(required = false) String services,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Float minRating) {
        
        try {
            List<Workshop> allWorkshops = workshopRepository.findAll().stream()
                .filter(Workshop::isActive)
                .collect(Collectors.toList());
            
            // Filter by minimum rating
            if (minRating != null) {
                allWorkshops = allWorkshops.stream()
                    .filter(w -> w.getTrustScore() != null && w.getTrustScore() >= minRating)
                    .collect(Collectors.toList());
            }
            
            // Filter by services
            if (services != null && !services.isBlank()) {
                Set<String> requiredServices = Set.of(services.split(","));
                allWorkshops = allWorkshops.stream()
                    .filter(w -> w.getServiceTemplates().stream()
                        .anyMatch(st -> st.getCatalogService() != null && 
                            requiredServices.contains(st.getCatalogService().name())))
                    .collect(Collectors.toList());
            }
            
            // Filter by tags
            if (tags != null && !tags.isBlank()) {
                Set<String> requiredTags = Set.of(tags.split(","));
                allWorkshops = allWorkshops.stream()
                    .filter(w -> w.getCapabilityTags().stream()
                        .anyMatch(tag -> requiredTags.contains(tag.name())))
                    .collect(Collectors.toList());
            }
            
            // Convert to search results with distance calculation
            List<WorkshopSearchResult> results = allWorkshops.stream()
                .map(workshop -> {
                    Double distance = null;
                    if (latitude != null && longitude != null && !workshop.getLocations().isEmpty()) {
                        // Calculate distance to closest location
                        distance = workshop.getLocations().stream()
                            .map(loc -> calculateDistance(latitude, longitude, 
                                loc.getCoordinates().latitude(), 
                                loc.getCoordinates().longitude()))
                            .min(Double::compareTo)
                            .orElse(null);
                    }
                    
                    String primaryLocation = workshop.getLocations().isEmpty() 
                        ? "No location" 
                        : formatAddress(workshop.getLocations().get(0).getAddress());
                    
                    return new WorkshopSearchResult(
                        workshop.getId(),
                        workshop.getName(),
                        workshop.getShortDescription(),
                        workshop.getLogoUrl(),
                        workshop.getTrustScore(),
                        workshop.getSubscriptionTier().name(),
                        workshop.getCapabilityTags().stream()
                            .map(CapabilityTag::name)
                            .collect(Collectors.toSet()),
                        distance,
                        primaryLocation
                    );
                })
                .collect(Collectors.toList());
            
            // Filter by distance if coordinates provided
            if (latitude != null && longitude != null) {
                results = results.stream()
                    .filter(r -> r.distance() != null && r.distance() <= radiusKm)
                    .sorted((a, b) -> Double.compare(a.distance(), b.distance()))
                    .collect(Collectors.toList());
            }
            
            return ResponseEntity.ok(results);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }
    
    /**
     * Get public profile of a workshop.
     * 
     * @param workshopId Workshop ID
     * @return Public workshop profile
     */
    @GetMapping("/{workshopId}/public")
    @Operation(summary = "Get public workshop profile", description = "Get detailed public information about a workshop")
    public ResponseEntity<PublicWorkshopProfile> getPublicWorkshopProfile(@PathVariable Long workshopId) {
        try {
            Workshop workshop = workshopRepository.findById(workshopId)
                .filter(Workshop::isActive)
                .orElse(null);
            
            if (workshop == null) {
                return ResponseEntity.notFound().build();
            }
            
            List<LocationResource> locationResources = workshop.getLocations().stream()
                .map(LocationResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
            
            List<ServiceTemplateResource> serviceResources = workshop.getServiceTemplates().stream()
                .map(ServiceTemplateResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
            
            PublicWorkshopProfile profile = new PublicWorkshopProfile(
                workshop.getId(),
                workshop.getName(),
                workshop.getShortDescription(),
                null, // Phone not exposed in Workshop entity
                null, // Email not exposed in Workshop entity
                workshop.getLogoUrl(),
                workshop.getPhotoUrls(),
                locationResources,
                serviceResources,
                workshop.getCapabilityTags().stream()
                    .map(CapabilityTag::name)
                    .collect(Collectors.toSet()),
                workshop.getTrustScore(),
                workshop.getSubscriptionTier().name(),
                null // No distance for direct profile view
            );
            
            return ResponseEntity.ok(profile);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get services offered by a workshop.
     * 
     * @param workshopId Workshop ID
     * @return List of service templates
     */
    @GetMapping("/{workshopId}/services")
    @Operation(summary = "Get workshop services", description = "Get list of services offered by a workshop")
    public ResponseEntity<List<ServiceTemplateResource>> getWorkshopServices(@PathVariable Long workshopId) {
        try {
            Workshop workshop = workshopRepository.findById(workshopId)
                .filter(Workshop::isActive)
                .orElse(null);
            
            if (workshop == null) {
                return ResponseEntity.notFound().build();
            }
            
            List<ServiceTemplateResource> services = workshop.getServiceTemplates().stream()
                .map(ServiceTemplateResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(services);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }
    
    /**
     * Get all available service categories.
     * @deprecated Use /api/v1/catalog/services/categories instead
     */
    @Deprecated
    @GetMapping("/catalog/categories")
    @Operation(summary = "Get service categories (deprecated)", description = "DEPRECATED: Use /api/v1/catalog/services/categories instead")
    public ResponseEntity<List<Map<String, String>>> getServiceCategories() {
        try {
            var categories = ServiceCategory.values();
            List<Map<String, String>> categoryList = new ArrayList<>();
            
            for (var category : categories) {
                Map<String, String> categoryMap = new HashMap<>();
                categoryMap.put("code", category.name());
                categoryMap.put("displayName", category.getDisplayName());
                categoryList.add(categoryMap);
            }
            
            return ResponseEntity.ok(categoryList);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }
    
    /**
     * Get all available services in the catalog.
     * @deprecated Use /api/v1/catalog/services instead
     */
    @Deprecated
    @GetMapping("/catalog/services")
    @Operation(summary = "Get service catalog (deprecated)", description = "DEPRECATED: Use /api/v1/catalog/services instead")
    public ResponseEntity<List<Map<String, Object>>> getServiceCatalog() {
        try {
            var services = ServiceCatalog.values();
            List<Map<String, Object>> serviceList = new ArrayList<>();
            
            for (var service : services) {
                Map<String, Object> serviceMap = new HashMap<>();
                serviceMap.put("code", service.name());
                serviceMap.put("displayName", service.getDisplayName());
                serviceMap.put("category", service.getCategory().name());
                serviceMap.put("categoryDisplayName", service.getCategory().getDisplayName());
                serviceList.add(serviceMap);
            }
            
            return ResponseEntity.ok(serviceList);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }
    
    /**
     * Get all available capability tags.
     * @deprecated Use /api/v1/catalog/capability-tags instead
     */
    @Deprecated
    @GetMapping("/catalog/capability-tags")
    @Operation(summary = "Get capability tags (deprecated)", description = "DEPRECATED: Use /api/v1/catalog/capability-tags instead")
    public ResponseEntity<List<Map<String, String>>> getCapabilityTags() {
        try {
            var tags = CapabilityTag.values();
            List<Map<String, String>> tagList = new ArrayList<>();
            
            for (var tag : tags) {
                Map<String, String> tagMap = new HashMap<>();
                tagMap.put("code", tag.name());
                tagMap.put("displayName", tag.getDisplayName());
                tagMap.put("category", tag.getCategory().name());
                tagList.add(tagMap);
            }
            
            return ResponseEntity.ok(tagList);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }
    
    /**
     * Format an Address object to a readable string.
     * 
     * @param address Address object
     * @return Formatted address string
     */
    private String formatAddress(com.atg.autonexo.backend.shared.domain.model.valueobjects.Address address) {
        return String.format("%s, %s, %s %s, %s", 
            address.street(), 
            address.city(), 
            address.state(), 
            address.zip(), 
            address.country());
    }
    
    /**
     * Calculate distance between two coordinates using Haversine formula.
     * 
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @return Distance in kilometers
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}

