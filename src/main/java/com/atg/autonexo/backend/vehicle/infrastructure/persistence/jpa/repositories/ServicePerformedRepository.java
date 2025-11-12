package com.atg.autonexo.backend.vehicle.infrastructure.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atg.autonexo.backend.vehicle.domain.model.entities.ServicePerformed;

/**
 * JPA repository for ServicePerformed entity.
 */
@Repository
public interface ServicePerformedRepository extends JpaRepository<ServicePerformed, Long> {
    
    /**
     * Find all services performed for a maintenance.
     */
    List<ServicePerformed> findByMaintenanceId(Long maintenanceId);
}

