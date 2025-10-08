package com.atg.autonexo.backend.workshop.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Value Object representing a unique identifier for a Location within the Workshop context.
 */
@Embeddable
public record LocationId(@Column(name = "location_id") Long id) {
    
    public LocationId {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("LocationId cannot be null or negative.");
        }
    }
}

