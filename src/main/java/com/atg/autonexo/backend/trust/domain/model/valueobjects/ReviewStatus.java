package com.atg.autonexo.backend.trust.domain.model.valueobjects;

/**
 * Enum representing the status of a review in its lifecycle.
 */
public enum ReviewStatus {
    /**
     * Review window is pending - service not yet completed
     */
    PENDING_WINDOW,
    
    /**
     * Review is available to be submitted - within 14 day window
     */
    AVAILABLE,
    
    /**
     * Review window has expired - 14 days passed without submission
     */
    EXPIRED,
    
    /**
     * Review has been submitted
     */
    SUBMITTED
}

