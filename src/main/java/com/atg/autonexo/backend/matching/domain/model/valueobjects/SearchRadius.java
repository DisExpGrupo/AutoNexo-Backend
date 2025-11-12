package com.atg.autonexo.backend.matching.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Value object representing search radius in kilometers.
 * Valid range: 1-50 km.
 */
@Embeddable
public record SearchRadius(@Column(name = "search_radius_km") Integer valueInKm) {
    
    private static final int MIN_RADIUS_KM = 1;
    private static final int MAX_RADIUS_KM = 50;
    
    public SearchRadius {
        if (valueInKm == null) {
            throw new IllegalArgumentException("Search radius cannot be null");
        }
        if (valueInKm < MIN_RADIUS_KM || valueInKm > MAX_RADIUS_KM) {
            throw new IllegalArgumentException(
                String.format("Search radius must be between %d and %d km", MIN_RADIUS_KM, MAX_RADIUS_KM)
            );
        }
    }
    
    /**
     * Checks if a distance (in km) is within this search radius.
     */
    public boolean isWithinRadius(double distanceKm) {
        return distanceKm <= this.valueInKm;
    }
    
    /**
     * Gets the minimum allowed radius.
     */
    public static int getMinRadius() {
        return MIN_RADIUS_KM;
    }
    
    /**
     * Gets the maximum allowed radius.
     */
    public static int getMaxRadius() {
        return MAX_RADIUS_KM;
    }
}

