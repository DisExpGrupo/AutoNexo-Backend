package com.atg.autonexo.backend.vehicle.infrastructure.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.atg.autonexo.backend.vehicle.domain.model.aggregates.Maintenance;
import com.atg.autonexo.backend.vehicle.domain.model.valueobjects.MaintenanceStatus;

/**
 * JPA repository for Maintenance aggregate.
 */
@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    
    /**
     * Find all maintenance records for a vehicle, ordered by date descending.
     */
    @Query("SELECT m FROM Maintenance m WHERE m.vehicleId = :vehicleId ORDER BY m.maintenanceDate DESC")
    List<Maintenance> findByVehicleIdOrderByDateDesc(@Param("vehicleId") Long vehicleId);
    
    /**
     * Find maintenance by ID.
     */
    Optional<Maintenance> findById(Long id);
    
    /**
     * Find pending maintenance confirmations for vehicles owned/authorized by user.
     */
    @Query("SELECT m FROM Maintenance m " +
           "JOIN VehicleOwnership vo ON m.vehicleId = vo.vehicleId " +
           "WHERE vo.userId.id = :userId AND m.status = :status " +
           "ORDER BY m.maintenanceDate DESC")
    List<Maintenance> findPendingByUserId(@Param("userId") Long userId, @Param("status") MaintenanceStatus status);
}

