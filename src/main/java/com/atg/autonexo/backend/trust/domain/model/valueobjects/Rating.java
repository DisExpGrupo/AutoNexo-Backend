package com.atg.autonexo.backend.trust.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Value Object representing a rating from 1 to 5 stars.
 */
@Embeddable
public record Rating(@Column(name = "rating") Integer value) {
    
    public Rating {
        if (value == null) {
            throw new IllegalArgumentException("Rating cannot be null");
        }
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5 stars");
        }
    }
    
    public static Rating of(int value) {
        return new Rating(value);
    }
}

