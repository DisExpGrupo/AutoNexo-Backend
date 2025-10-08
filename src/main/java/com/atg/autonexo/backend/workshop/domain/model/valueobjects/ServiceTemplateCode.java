package com.atg.autonexo.backend.workshop.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Value Object representing a unique service template code within a workshop.
 * Used for easier identification of service templates.
 */
@Embeddable
public record ServiceTemplateCode(@Column(name = "service_template_code") String value) {
    
    public ServiceTemplateCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Service template code cannot be null or blank.");
        }
        // Alphanumeric and hyphens only, 3-20 characters
        if (!value.matches("^[A-Z0-9-]{3,20}$")) {
            throw new IllegalArgumentException("Service template code must be 3-20 uppercase alphanumeric characters or hyphens.");
        }
    }
    
    @Override
    public String toString() {
        return value;
    }
}

