package com.atg.autonexo.backend.vehicle.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Value object representing vehicle mileage.
 * Must be non-negative integer.
 */
@Embeddable
public record Mileage(@Column(name = "mileage") Integer value) {
    
    public Mileage {
        if (value == null) {
            throw new IllegalArgumentException("Mileage cannot be null");
        }
        if (value < 0) {
            throw new IllegalArgumentException("Mileage cannot be negative");
        }
    }
    
    /**
     * Checks if this mileage is greater than another.
     */
    public boolean isGreaterThan(Mileage other) {
        return this.value > other.value;
    }
    
    /**
     * Checks if this mileage is greater than or equal to another.
     */
    public boolean isGreaterThanOrEqual(Mileage other) {
        return this.value >= other.value;
    }
}

