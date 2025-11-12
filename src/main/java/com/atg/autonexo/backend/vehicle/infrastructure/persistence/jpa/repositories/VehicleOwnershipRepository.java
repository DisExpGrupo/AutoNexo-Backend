package com.atg.autonexo.backend.vehicle.infrastructure.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.atg.autonexo.backend.vehicle.domain.model.entities.VehicleOwnership;
import com.atg.autonexo.backend.vehicle.domain.model.valueobjects.OwnershipType;

/**
 * JPA repository for VehicleOwnership entity.
 */
@Repository
public interface VehicleOwnershipRepository extends JpaRepository<VehicleOwnership, Long> {
    
    /**
     * Find all ownerships for a vehicle.
     */
    List<VehicleOwnership> findByVehicleId(Long vehicleId);
    
    /**
     * Find ownership by vehicle and user.
     */
    Optional<VehicleOwnership> findByVehicleIdAndUserIdId(Long vehicleId, Long userId);
    
    /**
     * Find primary ownership for a vehicle.
     */
    @Query("SELECT vo FROM VehicleOwnership vo WHERE vo.vehicleId = :vehicleId AND vo.ownershipType = :type")
    Optional<VehicleOwnership> findPrimaryOwnership(@Param("vehicleId") Long vehicleId, @Param("type") OwnershipType type);
    
    /**
     * Find all vehicles where user is owner or authorized.
     */
    @Query("SELECT vo.vehicleId FROM VehicleOwnership vo WHERE vo.userId.id = :userId")
    List<Long> findVehicleIdsByUserId(@Param("userId") Long userId);
    
    /**
     * Delete all ownerships for a vehicle (used during transfer).
     */
    void deleteByVehicleId(Long vehicleId);
    
    /**
     * Check if user is authorized for vehicle.
     */
    @Query("SELECT COUNT(vo) > 0 FROM VehicleOwnership vo WHERE vo.vehicleId = :vehicleId AND vo.userId.id = :userId")
    boolean isUserAuthorized(@Param("vehicleId") Long vehicleId, @Param("userId") Long userId);
}

