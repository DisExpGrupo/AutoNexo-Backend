package com.atg.autonexo.backend.vehicle.domain.model.entities;

import java.math.BigDecimal;

import com.atg.autonexo.backend.shared.domain.model.entities.AuditableModel;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.ServiceCatalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a service performed during a maintenance.
 * Part of the Maintenance aggregate.
 */
@Entity
@Getter
@Setter
public class ServicePerformed extends AuditableModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ServiceCatalog serviceType;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;
    
    // Reference to Maintenance aggregate (many-to-one)
    @Column(nullable = true) // Can be null temporarily until Maintenance is saved
    private Long maintenanceId;
    
    protected ServicePerformed() {}
    
    public ServicePerformed(ServiceCatalog serviceType, String description, BigDecimal cost, Long maintenanceId) {
        if (serviceType == null) {
            throw new IllegalArgumentException("ServiceType cannot be null");
        }
        if (cost == null || cost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cost cannot be null or negative");
        }
        // maintenanceId can be null temporarily (will be set after Maintenance is saved)
        
        this.serviceType = serviceType;
        this.description = description;
        this.cost = cost;
        this.maintenanceId = maintenanceId;
    }
}

