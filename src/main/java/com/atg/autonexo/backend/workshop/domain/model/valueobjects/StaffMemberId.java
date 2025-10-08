package com.atg.autonexo.backend.workshop.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Value Object representing a unique identifier for a StaffMember within the Workshop context.
 */
@Embeddable
public record StaffMemberId(@Column(name = "staff_member_id") Long id) {
    
    public StaffMemberId {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("StaffMemberId cannot be null or negative.");
        }
    }
}

