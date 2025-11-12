package com.atg.autonexo.backend.trust.domain.model.valueobjects;

/**
 * Enum representing the type of review based on who is reviewing whom.
 */
public enum ReviewType {
    /**
     * A car owner reviewing a workshop
     */
    USER_TO_WORKSHOP,
    
    /**
     * A workshop reviewing a car owner
     */
    WORKSHOP_TO_USER
}

