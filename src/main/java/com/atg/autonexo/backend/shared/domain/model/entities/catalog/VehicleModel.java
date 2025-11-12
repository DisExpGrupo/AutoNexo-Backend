package com.atg.autonexo.backend.shared.domain.model.entities.catalog;

import com.atg.autonexo.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Vehicle Model aggregate root.
 * Represents a specific vehicle model from a brand.
 */
@Entity
@Table(name = "vehicle_model")
@Getter
@Setter
public class VehicleModel extends AuditableAbstractAggregateRoot<VehicleModel> {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private VehicleBrand brand;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column
    private Integer startYear;
    
    @Column
    private Integer endYear;
    
    @Column(nullable = false)
    private boolean isActive = true;
    
    protected VehicleModel() {}
    
    /**
     * Creates a new vehicle model.
     */
    public VehicleModel(VehicleBrand brand, String name, Integer startYear, Integer endYear) {
        if (brand == null) {
            throw new IllegalArgumentException("Brand cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Model name cannot be null or empty");
        }
        if (startYear != null && (startYear < 1900 || startYear > 2100)) {
            throw new IllegalArgumentException("Start year must be between 1900 and 2100");
        }
        if (endYear != null && (endYear < 1900 || endYear > 2100)) {
            throw new IllegalArgumentException("End year must be between 1900 and 2100");
        }
        if (startYear != null && endYear != null && endYear < startYear) {
            throw new IllegalArgumentException("End year cannot be before start year");
        }
        
        this.brand = brand;
        this.name = name.trim();
        this.startYear = startYear;
        this.endYear = endYear;
        this.isActive = true;
    }
    
    /**
     * Activates the model.
     */
    public void activate() {
        this.isActive = true;
    }
    
    /**
     * Deactivates the model (soft delete).
     */
    public void deactivate() {
        this.isActive = false;
    }
    
    /**
     * Updates model information.
     */
    public void update(String name, Integer startYear, Integer endYear) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
        if (startYear != null && (startYear < 1900 || startYear > 2100)) {
            throw new IllegalArgumentException("Start year must be between 1900 and 2100");
        }
        if (endYear != null && (endYear < 1900 || endYear > 2100)) {
            throw new IllegalArgumentException("End year must be between 1900 and 2100");
        }
        if (startYear != null && endYear != null && endYear < startYear) {
            throw new IllegalArgumentException("End year cannot be before start year");
        }
        
        this.startYear = startYear;
        this.endYear = endYear;
    }
}

