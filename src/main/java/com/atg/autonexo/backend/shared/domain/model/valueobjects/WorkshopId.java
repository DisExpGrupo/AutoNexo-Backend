package com.atg.autonexo.backend.shared.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Represents the immutable unique identifier for a Workshop across all Bounded Contexts.
 * This Value Object is used by other aggregates for referencing a Workshop and
 * by the Infrastructure layer (WorkshopContext) to manage the execution scope.
 */
@Embeddable
public record WorkshopId(@Column(name = "workshop_id") Long id) {
    
    public WorkshopId {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("WorkshopId cannot be null or negative.");
        }
    }
}