package com.atg.autonexo.backend.shared.domain.model.entities.catalog;

import com.atg.autonexo.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Vehicle Brand aggregate root.
 * Represents a vehicle manufacturer brand in the catalog.
 */
@Entity
@Table(name = "vehicle_brand")
@Getter
@Setter
public class VehicleBrand extends AuditableAbstractAggregateRoot<VehicleBrand> {
    
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    
    @Column(length = 500)
    private String logoUrl;
    
    @Column(length = 100)
    private String country;
    
    @Column(nullable = false)
    private boolean isActive = true;
    
    @Column(nullable = false)
    private boolean popular = false;
    
    protected VehicleBrand() {}
    
    /**
     * Creates a new vehicle brand.
     */
    public VehicleBrand(String name, String logoUrl, String country, boolean popular) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Brand name cannot be null or empty");
        }
        this.name = name.trim();
        this.logoUrl = logoUrl;
        this.country = country;
        this.popular = popular;
        this.isActive = true;
    }
    
    /**
     * Activates the brand.
     */
    public void activate() {
        this.isActive = true;
    }
    
    /**
     * Deactivates the brand (soft delete).
     */
    public void deactivate() {
        this.isActive = false;
    }
    
    /**
     * Marks the brand as popular.
     */
    public void markAsPopular() {
        this.popular = true;
    }
    
    /**
     * Removes popular marking.
     */
    public void unmarkAsPopular() {
        this.popular = false;
    }
    
    /**
     * Updates brand information.
     */
    public void update(String name, String logoUrl, String country, boolean popular) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
        this.logoUrl = logoUrl;
        this.country = country;
        this.popular = popular;
    }
}

