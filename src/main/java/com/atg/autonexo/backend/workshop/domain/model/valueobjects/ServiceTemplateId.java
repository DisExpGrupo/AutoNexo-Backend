package com.atg.autonexo.backend.workshop.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Value Object representing a unique identifier for a ServiceTemplate within the Workshop context.
 */
@Embeddable
public record ServiceTemplateId(@Column(name = "service_template_id") Long id) {
    
    public ServiceTemplateId {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ServiceTemplateId cannot be null or negative.");
        }
    }
}

