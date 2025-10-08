package com.atg.autonexo.backend.workshop.domain.model.entities;

import com.atg.autonexo.backend.shared.domain.model.entities.AuditableModel;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * StaffMember entity representing an employee working at the workshop.
 * Part of the Workshop aggregate.
 */
@Entity
@Getter
@Setter
public class StaffMember extends AuditableModel {
    
    @Embedded
    private UserId userId;
    
    @Column(name = "primary_location_id")
    private Long primaryLocationId;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "staff_member_other_locations", joinColumns = @JoinColumn(name = "staff_member_id"))
    @Column(name = "location_id")
    private List<Long> otherLocationIds = new ArrayList<>();
    
    @Column(nullable = false)
    private boolean active = true;
    
    protected StaffMember() {}
    
    public StaffMember(UserId userId, Long primaryLocationId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.userId = userId;
        this.primaryLocationId = primaryLocationId;
        this.otherLocationIds = new ArrayList<>();
        this.active = true;
    }
    
    /**
     * Adds an additional location where this staff member can work
     */
    public void addOtherLocation(Long locationId) {
        if (locationId != null && !this.otherLocationIds.contains(locationId)) {
            this.otherLocationIds.add(locationId);
        }
    }
    
    /**
     * Removes an additional location
     */
    public void removeOtherLocation(Long locationId) {
        if (locationId != null) {
            this.otherLocationIds.remove(locationId);
        }
    }
    
    /**
     * Changes the primary location
     */
    public void changePrimaryLocation(Long newPrimaryLocationId) {
        this.primaryLocationId = newPrimaryLocationId;
    }
    
    /**
     * Checks if this staff member works at a specific location
     */
    public boolean worksAtLocation(Long locationId) {
        if (locationId == null) {
            return false;
        }
        return locationId.equals(this.primaryLocationId) || this.otherLocationIds.contains(locationId);
    }
    
    /**
     * Deactivates this staff member
     */
    public void deactivate() {
        this.active = false;
    }
    
    /**
     * Activates this staff member
     */
    public void activate() {
        this.active = true;
    }
    
    /**
     * Checks if this staff member belongs to a specific user
     */
    public boolean belongsToUser(Long userId) {
        return userId != null && this.userId.id().equals(userId);
    }
}
