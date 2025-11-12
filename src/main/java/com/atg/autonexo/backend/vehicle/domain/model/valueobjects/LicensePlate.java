package com.atg.autonexo.backend.vehicle.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Value object representing a vehicle license plate.
 * Validates basic format (not empty, reasonable length).
 */
@Embeddable
public record LicensePlate(@Column(name = "license_plate", length = 20) String value) {
    
    public LicensePlate {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be null or empty");
        }
        if (value.length() > 20) {
            throw new IllegalArgumentException("License plate cannot exceed 20 characters");
        }
    }
}

