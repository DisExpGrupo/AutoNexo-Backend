package com.atg.autonexo.backend.workshop.domain.model.valueobjects;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;

/**
 * Value Object representing business registration information (RUC in Peru).
 * Contains the registration number and basic verification status.
 */
@Embeddable
public record BusinessRegistration(
    String ruc, 
    boolean verifiedBasic, 
    LocalDateTime verifiedAt
) {
    
    public BusinessRegistration {
        if (ruc == null || ruc.isBlank()) {
            throw new IllegalArgumentException("RUC cannot be null or blank.");
        }
        // Basic RUC validation (11 digits for Peru)
        if (!ruc.matches("^\\d{11}$")) {
            throw new IllegalArgumentException("RUC must be 11 digits.");
        }
        // If verified, verifiedAt must be present
        if (verifiedBasic && verifiedAt == null) {
            throw new IllegalArgumentException("Verified date must be present when verified is true.");
        }
    }
    
    /**
     * Creates an unverified business registration
     */
    public static BusinessRegistration unverified(String ruc) {
        return new BusinessRegistration(ruc, false, null);
    }
    
    /**
     * Marks this registration as verified
     */
    public BusinessRegistration markAsVerified() {
        return new BusinessRegistration(this.ruc, true, LocalDateTime.now());
    }
}

