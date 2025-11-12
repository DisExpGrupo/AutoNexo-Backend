package com.atg.autonexo.backend.shared.infrastructure.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atg.autonexo.backend.shared.domain.model.entities.catalog.VehicleBrand;

/**
 * Repository for VehicleBrand entity.
 */
@Repository
public interface VehicleBrandRepository extends JpaRepository<VehicleBrand, Long> {
    
    /**
     * Find all active brands.
     */
    List<VehicleBrand> findByIsActiveTrue();
    
    /**
     * Find all active and popular brands.
     */
    List<VehicleBrand> findByIsActiveTrueAndPopularTrue();
    
    /**
     * Find brand by name.
     */
    Optional<VehicleBrand> findByNameIgnoreCase(String name);
    
    /**
     * Check if brand exists by name.
     */
    boolean existsByNameIgnoreCase(String name);
}

