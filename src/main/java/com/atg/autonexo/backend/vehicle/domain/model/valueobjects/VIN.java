package com.atg.autonexo.backend.vehicle.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Value object representing a Vehicle Identification Number (VIN).
 * VIN is optional but if provided should be 17 characters (standard VIN length).
 */
@Embeddable
public record VIN(@Column(name = "vin", length = 17) String value) {
    
    public VIN {
        if (value != null) {
            String trimmed = value.trim();
            if (trimmed.length() != 17) {
                throw new IllegalArgumentException("VIN must be exactly 17 characters if provided");
            }
        }
    }
    
    /**
     * Creates a VIN from a string value, allowing null.
     */
    public static VIN of(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return new VIN(value);
    }
}

