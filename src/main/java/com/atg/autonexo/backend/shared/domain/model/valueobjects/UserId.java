package com.atg.autonexo.backend.shared.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
/**
 * Represents the immutable unique identifier for a User across all Bounded Contexts.
 * This Value Object is used by other aggregates for referencing a User.
 */
@Embeddable
public record UserId(@Column(name = "user_id")Long id) {
    
    public UserId {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("UserId cannot be null or negative.");
        }
    }

}
