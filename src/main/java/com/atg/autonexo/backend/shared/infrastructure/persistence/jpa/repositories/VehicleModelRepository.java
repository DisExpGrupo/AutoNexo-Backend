package com.atg.autonexo.backend.shared.infrastructure.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atg.autonexo.backend.shared.domain.model.entities.catalog.VehicleModel;

/**
 * Repository for VehicleModel entity.
 */
@Repository
public interface VehicleModelRepository extends JpaRepository<VehicleModel, Long> {
    
    /**
     * Find all active models for a specific brand.
     */
    List<VehicleModel> findByBrand_IdAndIsActiveTrue(Long brandId);
    
    /**
     * Find all models for a specific brand (including inactive).
     */
    List<VehicleModel> findByBrand_Id(Long brandId);
    
    /**
     * Check if model exists for a brand.
     */
    boolean existsByBrand_IdAndNameIgnoreCase(Long brandId, String name);
}

