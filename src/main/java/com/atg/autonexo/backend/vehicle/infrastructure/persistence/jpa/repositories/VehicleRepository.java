package com.atg.autonexo.backend.vehicle.infrastructure.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.atg.autonexo.backend.vehicle.domain.model.aggregates.Vehicle;

/**
 * JPA repository for Vehicle aggregate.
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    /**
     * Find all active vehicles owned by a user (primary owner).
     */
    @Query("SELECT v FROM Vehicle v WHERE v.primaryOwnerId.id = :userId AND v.active = true")
    List<Vehicle> findByPrimaryOwnerId(@Param("userId") Long userId);
    
    /**
     * Find vehicle by ID if user is primary owner or authorized.
     */
    @Query("SELECT v FROM Vehicle v WHERE v.id = :vehicleId AND v.active = true")
    Optional<Vehicle> findByIdAndActive(@Param("vehicleId") Long vehicleId);
    
    /**
     * Check if license plate already exists for active vehicles.
     */
    @Query("SELECT COUNT(v) > 0 FROM Vehicle v WHERE v.licensePlate.value = :licensePlate AND v.active = true")
    boolean existsByLicensePlate(@Param("licensePlate") String licensePlate);
}

