package com.atg.autonexo.backend.shared.interfaces.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atg.autonexo.backend.shared.domain.model.entities.catalog.VehicleBrand;
import com.atg.autonexo.backend.shared.domain.model.entities.catalog.VehicleModel;
import com.atg.autonexo.backend.shared.infrastructure.persistence.jpa.repositories.VehicleBrandRepository;
import com.atg.autonexo.backend.shared.infrastructure.persistence.jpa.repositories.VehicleModelRepository;
import com.atg.autonexo.backend.shared.interfaces.rest.resources.catalog.CreateVehicleBrandResource;
import com.atg.autonexo.backend.shared.interfaces.rest.resources.catalog.CreateVehicleModelResource;
import com.atg.autonexo.backend.shared.interfaces.rest.resources.catalog.UpdateVehicleBrandResource;
import com.atg.autonexo.backend.shared.interfaces.rest.resources.catalog.UpdateVehicleModelResource;
import com.atg.autonexo.backend.shared.interfaces.rest.resources.catalog.VehicleBrandResource;
import com.atg.autonexo.backend.shared.interfaces.rest.resources.catalog.VehicleModelResource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for catalog administration (brands and models).
 * Requires ADMIN role.
 */
@RestController
@RequestMapping("/api/v1/admin/catalog")
@RequiredArgsConstructor
@Validated
@Tag(name = "Catalog Administration", description = "Admin endpoints for managing vehicle brands and models")
@PreAuthorize("hasRole('ADMIN')")
public class CatalogAdminController {
    
    private final VehicleBrandRepository vehicleBrandRepository;
    private final VehicleModelRepository vehicleModelRepository;
    
    // ==================== BRAND ENDPOINTS ====================
    
    /**
     * Get all brands (including inactive).
     */
    @GetMapping("/brands")
    @Operation(summary = "Get all brands", description = "Get all vehicle brands including inactive ones")
    public ResponseEntity<List<VehicleBrandResource>> getAllBrands() {
        List<VehicleBrandResource> brands = vehicleBrandRepository.findAll().stream()
            .map(this::toResource)
            .collect(Collectors.toList());
        return ResponseEntity.ok(brands);
    }
    
    /**
     * Get brand by ID.
     */
    @GetMapping("/brands/{id}")
    @Operation(summary = "Get brand by ID", description = "Get a specific vehicle brand by its ID")
    public ResponseEntity<VehicleBrandResource> getBrandById(@PathVariable Long id) {
        return vehicleBrandRepository.findById(id)
            .map(brand -> ResponseEntity.ok(toResource(brand)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create a new vehicle brand.
     */
    @PostMapping("/brands")
    @Operation(summary = "Create brand", description = "Create a new vehicle brand")
    public ResponseEntity<VehicleBrandResource> createBrand(@Valid @RequestBody CreateVehicleBrandResource resource) {
        // Check if brand with same name already exists
        if (vehicleBrandRepository.existsByNameIgnoreCase(resource.name())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        VehicleBrand brand = new VehicleBrand(
            resource.name(),
            resource.logoUrl(),
            resource.country(),
            resource.popular()
        );
        
        VehicleBrand savedBrand = vehicleBrandRepository.save(brand);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResource(savedBrand));
    }
    
    /**
     * Update an existing vehicle brand.
     */
    @PutMapping("/brands/{id}")
    @Operation(summary = "Update brand", description = "Update an existing vehicle brand")
    public ResponseEntity<VehicleBrandResource> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVehicleBrandResource resource) {
        
        return vehicleBrandRepository.findById(id)
            .map(brand -> {
                // Check if another brand with same name exists
                vehicleBrandRepository.findByNameIgnoreCase(resource.name())
                    .ifPresent(existingBrand -> {
                        if (!existingBrand.getId().equals(id)) {
                            throw new IllegalArgumentException("Brand with this name already exists");
                        }
                    });
                
                brand.update(resource.name(), resource.logoUrl(), resource.country(), resource.popular());
                VehicleBrand savedBrand = vehicleBrandRepository.save(brand);
                return ResponseEntity.ok(toResource(savedBrand));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Delete (deactivate) a vehicle brand.
     */
    @DeleteMapping("/brands/{id}")
    @Operation(summary = "Delete brand", description = "Soft delete (deactivate) a vehicle brand")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        return vehicleBrandRepository.findById(id)
            .map(brand -> {
                brand.deactivate();
                vehicleBrandRepository.save(brand);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    // ==================== MODEL ENDPOINTS ====================
    
    /**
     * Get all models for a specific brand.
     */
    @GetMapping("/brands/{brandId}/models")
    @Operation(summary = "Get models by brand", description = "Get all vehicle models for a specific brand")
    public ResponseEntity<List<VehicleModelResource>> getModelsByBrand(@PathVariable Long brandId) {
        // Check if brand exists
        if (!vehicleBrandRepository.existsById(brandId)) {
            return ResponseEntity.notFound().build();
        }
        
        List<VehicleModelResource> models = vehicleModelRepository.findByBrand_Id(brandId).stream()
            .map(this::toResource)
            .collect(Collectors.toList());
        return ResponseEntity.ok(models);
    }
    
    /**
     * Get model by ID.
     */
    @GetMapping("/models/{id}")
    @Operation(summary = "Get model by ID", description = "Get a specific vehicle model by its ID")
    public ResponseEntity<VehicleModelResource> getModelById(@PathVariable Long id) {
        return vehicleModelRepository.findById(id)
            .map(model -> ResponseEntity.ok(toResource(model)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create a new vehicle model.
     */
    @PostMapping("/models")
    @Operation(summary = "Create model", description = "Create a new vehicle model")
    public ResponseEntity<VehicleModelResource> createModel(@Valid @RequestBody CreateVehicleModelResource resource) {
        // Check if brand exists
        VehicleBrand brand = vehicleBrandRepository.findById(resource.brandId())
            .orElse(null);
        if (brand == null) {
            return ResponseEntity.badRequest().build();
        }
        
        // Check if model with same name already exists for this brand
        if (vehicleModelRepository.existsByBrand_IdAndNameIgnoreCase(resource.brandId(), resource.name())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        VehicleModel model = new VehicleModel(
            brand,
            resource.name(),
            resource.startYear(),
            resource.endYear()
        );
        
        VehicleModel savedModel = vehicleModelRepository.save(model);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResource(savedModel));
    }
    
    /**
     * Update an existing vehicle model.
     */
    @PutMapping("/models/{id}")
    @Operation(summary = "Update model", description = "Update an existing vehicle model")
    public ResponseEntity<VehicleModelResource> updateModel(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVehicleModelResource resource) {
        
        return vehicleModelRepository.findById(id)
            .map(model -> {
                model.update(resource.name(), resource.startYear(), resource.endYear());
                VehicleModel savedModel = vehicleModelRepository.save(model);
                return ResponseEntity.ok(toResource(savedModel));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Delete (deactivate) a vehicle model.
     */
    @DeleteMapping("/models/{id}")
    @Operation(summary = "Delete model", description = "Soft delete (deactivate) a vehicle model")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        return vehicleModelRepository.findById(id)
            .map(model -> {
                model.deactivate();
                vehicleModelRepository.save(model);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
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
    
    private VehicleModelResource toResource(VehicleModel model) {
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

