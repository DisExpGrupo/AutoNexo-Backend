package com.atg.autonexo.backend.shared.interfaces.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atg.autonexo.backend.shared.domain.model.entities.catalog.VehicleBrand;
import com.atg.autonexo.backend.shared.domain.model.entities.catalog.VehicleModel;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.CapabilityTag;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCategory;
import com.atg.autonexo.backend.shared.infrastructure.persistence.jpa.repositories.VehicleBrandRepository;
import com.atg.autonexo.backend.shared.infrastructure.persistence.jpa.repositories.VehicleModelRepository;
import com.atg.autonexo.backend.shared.interfaces.rest.resources.catalog.VehicleBrandResource;
import com.atg.autonexo.backend.shared.interfaces.rest.resources.catalog.VehicleModelResource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Public REST controller for catalog endpoints.
 * These endpoints are accessible without authentication.
 */
@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
@Tag(name = "Public Catalog", description = "Public catalog endpoints for brands, models, services, and tags")
public class PublicCatalogController {
    
    private final VehicleBrandRepository vehicleBrandRepository;
    private final VehicleModelRepository vehicleModelRepository;
    
    // ==================== BRAND & MODEL ENDPOINTS ====================
    
    /**
     * Get all active vehicle brands.
     * 
     * @param popularOnly If true, returns only popular brands
     * @return List of active vehicle brands
     */
    @GetMapping("/brands")
    @Operation(summary = "Get vehicle brands", description = "Get all active vehicle brands, optionally filtered by popularity")
    public ResponseEntity<List<VehicleBrandResource>> getBrands(
            @RequestParam(required = false, defaultValue = "false") boolean popularOnly) {
        try {
            List<VehicleBrand> brands = popularOnly 
                ? vehicleBrandRepository.findByIsActiveTrueAndPopularTrue()
                : vehicleBrandRepository.findByIsActiveTrue();
            
            List<VehicleBrandResource> resources = brands.stream()
                .map(this::toResource)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(resources);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }
    
    /**
     * Get vehicle models for a specific brand.
     * 
     * @param brandId Brand ID
     * @return List of active vehicle models
     */
    @GetMapping("/brands/{brandId}/models")
    @Operation(summary = "Get vehicle models", description = "Get all active vehicle models for a specific brand")
    public ResponseEntity<List<VehicleModelResource>> getModelsByBrand(@PathVariable Long brandId) {
        try {
            // Check if brand exists and is active
            VehicleBrand brand = vehicleBrandRepository.findById(brandId)
                .filter(VehicleBrand::isActive)
                .orElse(null);
            
            if (brand == null) {
                return ResponseEntity.notFound().build();
            }
            
            List<VehicleModel> models = vehicleModelRepository.findByBrand_IdAndIsActiveTrue(brandId);
            
            List<VehicleModelResource> resources = models.stream()
                .map(this::toModelResource)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(resources);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }
    
    // ==================== SERVICE CATALOG ENDPOINTS ====================
    
    /**
     * Get all available services in the catalog.
     * 
     * @param category Optional category filter
     * @return List of services
     */
    @GetMapping("/services")
    @Operation(summary = "Get service catalog", description = "Get all available services in the system catalog")
    public ResponseEntity<List<Map<String, Object>>> getServiceCatalog(
            @RequestParam(required = false) String category) {
        try {
            ServiceCatalog[] services = ServiceCatalog.values();
            List<Map<String, Object>> serviceList = new ArrayList<>();
            
            for (ServiceCatalog service : services) {
                // Filter by category if provided
                if (category != null && !category.isBlank()) {
                    try {
                        ServiceCategory requestedCategory = ServiceCategory.valueOf(category.toUpperCase());
                        if (!service.getCategory().equals(requestedCategory)) {
                            continue;
                        }
                    } catch (IllegalArgumentException e) {
                        // Invalid category, skip filtering
                    }
                }
                
                Map<String, Object> serviceMap = new HashMap<>();
                serviceMap.put("code", service.name());
                serviceMap.put("displayName", service.getDisplayName());
                serviceMap.put("description", service.getDefaultDescription());
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
     * Get all service categories.
     * 
     * @return List of service categories
     */
    @GetMapping("/services/categories")
    @Operation(summary = "Get service categories", description = "Get all available service categories in the system")
    public ResponseEntity<List<Map<String, String>>> getServiceCategories() {
        try {
            ServiceCategory[] categories = ServiceCategory.values();
            List<Map<String, String>> categoryList = new ArrayList<>();
            
            for (ServiceCategory category : categories) {
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
    
    // ==================== CAPABILITY TAG ENDPOINTS ====================
    
    /**
     * Get all available capability tags.
     * 
     * @param category Optional tag category filter
     * @return List of capability tags
     */
    @GetMapping("/capability-tags")
    @Operation(summary = "Get capability tags", description = "Get all available capability tags in the system")
    public ResponseEntity<List<Map<String, String>>> getCapabilityTags(
            @RequestParam(required = false) String category) {
        try {
            CapabilityTag[] tags = CapabilityTag.values();
            List<Map<String, String>> tagList = new ArrayList<>();
            
            for (CapabilityTag tag : tags) {
                // Filter by category if provided
                if (category != null && !category.isBlank()) {
                    try {
                        CapabilityTag.TagCategory requestedCategory = 
                            CapabilityTag.TagCategory.valueOf(category.toUpperCase());
                        if (!tag.getCategory().equals(requestedCategory)) {
                            continue;
                        }
                    } catch (IllegalArgumentException e) {
                        // Invalid category, skip filtering
                    }
                }
                
                Map<String, String> tagMap = new HashMap<>();
                tagMap.put("code", tag.name());
                tagMap.put("displayName", tag.getDisplayName());
                tagMap.put("category", tag.getCategory().name());
                tagMap.put("categoryDisplayName", tag.getCategory().getDisplayName());
                tagList.add(tagMap);
            }
            
            return ResponseEntity.ok(tagList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }
    
    /**
     * Get all capability tag categories.
     * 
     * @return List of tag categories
     */
    @GetMapping("/capability-tags/categories")
    @Operation(summary = "Get capability tag categories", description = "Get all available capability tag categories")
    public ResponseEntity<List<Map<String, String>>> getCapabilityTagCategories() {
        try {
            CapabilityTag.TagCategory[] categories = CapabilityTag.TagCategory.values();
            List<Map<String, String>> categoryList = new ArrayList<>();
            
            for (CapabilityTag.TagCategory category : categories) {
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
    
    // ==================== HELPER METHODS ====================
    
    private VehicleBrandResource toResource(VehicleBrand brand) {
        return new VehicleBrandResource(
            brand.getId(),
            brand.getName(),
            brand.getLogoUrl(),
            brand.getCountry(),
            brand.isActive(),
            brand.isPopular()
        );
    }
    
    private VehicleModelResource toModelResource(VehicleModel model) {
        return new VehicleModelResource(
            model.getId(),
            model.getBrand().getId(),
            model.getName(),
            model.getStartYear(),
            model.getEndYear(),
            model.isActive()
        );
    }
}

