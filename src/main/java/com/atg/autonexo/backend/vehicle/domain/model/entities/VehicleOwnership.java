package com.atg.autonexo.backend.vehicle.domain.model.entities;

import java.time.LocalDateTime;

import com.atg.autonexo.backend.shared.domain.model.entities.AuditableModel;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;
import com.atg.autonexo.backend.vehicle.domain.model.valueobjects.OwnershipType;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a user's ownership relationship with a vehicle.
 * Tracks primary owner and authorized users.
 */
@Entity
@Getter
@Setter
public class VehicleOwnership extends AuditableModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Embedded
    private UserId userId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OwnershipType ownershipType;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime addedAt;
    
    // Reference to Vehicle aggregate (many-to-one)
    @Column(nullable = false)
    private Long vehicleId;
    
    protected VehicleOwnership() {}
    
    public VehicleOwnership(UserId userId, OwnershipType ownershipType, Long vehicleId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        if (ownershipType == null) {
            throw new IllegalArgumentException("OwnershipType cannot be null");
        }
        if (vehicleId == null || vehicleId <= 0) {
            throw new IllegalArgumentException("VehicleId must be valid");
        }
        
        this.userId = userId;
        this.ownershipType = ownershipType;
        this.vehicleId = vehicleId;
        this.addedAt = LocalDateTime.now();
    }
    
    /**
     * Checks if this ownership is for a specific user.
     */
    public boolean isForUser(Long userId) {
        return this.userId.id().equals(userId);
    }
    
    /**
     * Checks if this is a primary ownership.
     */
    public boolean isPrimary() {
        return ownershipType == OwnershipType.PRIMARY;
    }
    
    /**
     * Checks if this is an authorized ownership.
     */
    public boolean isAuthorized() {
        return ownershipType == OwnershipType.AUTHORIZED;
    }
}

