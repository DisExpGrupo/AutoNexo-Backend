package com.atg.autonexo.backend.workshop.infrastructure.persistence.jpa.repositories;

import com.atg.autonexo.backend.workshop.domain.model.aggregates.Workshop;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.CapabilityTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for Workshop aggregate
 */
@Repository
public interface WorkshopRepository extends JpaRepository<Workshop, Long> {
    
    /**
     * Finds a workshop by owner user ID
     */
    @Query("SELECT w FROM Workshop w WHERE w.ownerUserId.id = :ownerUserId")
    Optional<Workshop> findByOwnerUserId(@Param("ownerUserId") Long ownerUserId);
    
    /**
     * Checks if a workshop exists for a specific owner
     */
    @Query("SELECT COUNT(w) > 0 FROM Workshop w WHERE w.ownerUserId.id = :ownerUserId")
    boolean existsByOwnerUserId(@Param("ownerUserId") Long ownerUserId);
    
    /**
     * Finds all active workshops
     */
    List<Workshop> findByActiveTrue();
    
    /**
     * Finds workshops by capability tag
     */
    @Query("SELECT DISTINCT w FROM Workshop w JOIN w.capabilityTags t WHERE t = :tag AND w.active = true")
    List<Workshop> findByCapabilityTag(@Param("tag") CapabilityTag tag);
    
    /**
     * Finds workshops by trust score range
     */
    @Query("SELECT w FROM Workshop w WHERE w.trustScore >= :minScore AND w.active = true")
    List<Workshop> findByTrustScoreGreaterThanEqual(@Param("minScore") Float minScore);
}

